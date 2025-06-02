package com.example.picflow.file.service;

import com.example.picflow.file.dto.FileUploadResponse;
import com.example.picflow.global.config.FileStorageProperties;
import com.example.picflow.global.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileStorageProperties fileStorageProperties;
    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileUploadException("파일을 저장할 디렉토리를 생성할 수 없습니다.", ex);
        }
    }

    public FileUploadResponse storeFile(MultipartFile file) {
        // 파일명 정리
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // 파일명에 부적절한 문자가 있는지 확인
            if (originalFileName.contains("..")) {
                throw new FileUploadException("파일명에 부적절한 경로가 포함되어 있습니다: " + originalFileName);
            }

            // 파일 확장자 검증 (이미지만 허용)
            String fileExtension = getFileExtension(originalFileName);
            if (!isValidImageExtension(fileExtension)) {
                throw new FileUploadException("지원하지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 허용)");
            }

            // 고유한 파일명 생성
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;

            // 파일 저장
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 다운로드 URL 생성
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(fileName)
                    .toUriString();

            // 조회 URL 생성 (이미지 표시용)
            String fileViewUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/view/")
                    .path(fileName)
                    .toUriString();

            return FileUploadResponse.create(
                    fileName,
                    fileDownloadUri,
                    fileViewUri,
                    file.getContentType(),
                    file.getSize()
            );

        } catch (IOException ex) {
            throw new FileUploadException("파일을 저장할 수 없습니다: " + originalFileName, ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileUploadException("파일을 찾을 수 없습니다: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileUploadException("파일을 찾을 수 없습니다: " + fileName, ex);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidImageExtension(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg") ||
                extension.equals("png") || extension.equals("gif");
    }
}
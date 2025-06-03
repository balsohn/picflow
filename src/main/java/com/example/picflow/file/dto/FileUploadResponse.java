package com.example.picflow.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileViewUri;
    private String fileType;
    private long size;

    public static FileUploadResponse create(String fileName, String fileDownloadUri,
                                            String fileViewUri, String fileType, long size) {
        return new FileUploadResponse(fileName, fileDownloadUri, fileViewUri, fileType, size);
    }
}

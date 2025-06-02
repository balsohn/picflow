package com.example.picflow.post.service;

import com.example.picflow.follow.repository.FollowRepository;
import com.example.picflow.global.exception.PostNotFoundException;
import com.example.picflow.global.exception.UnauthorizedException;
import com.example.picflow.global.exception.UserNotFoundException;
import com.example.picflow.post.dto.PostCreateRequest;
import com.example.picflow.post.dto.PostPageResponse;
import com.example.picflow.post.dto.PostResponse;
import com.example.picflow.post.dto.PostUpdateRequest;
import com.example.picflow.post.entity.Post;
import com.example.picflow.post.repository.PostRepository;
import com.example.picflow.user.entity.User;
import com.example.picflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public PostResponse createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = Post.create(
                request.getTitle(),
                request.getContent(),
                request.getImageUrl(),
                user
        );

        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    public PostResponse getPost(Long userId) {
        Post post = postRepository.findById(userId)
                .orElseThrow(PostNotFoundException::new);
        return PostResponse.from(post);
    }

    public PostPageResponse getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<PostResponse> postResponses = posts.map(PostResponse::from);
        return PostPageResponse.from(postResponses);
    }

    // 뉴스피드 조회 (팔로우한 사용자들의 게시물)
    public PostPageResponse getNewsFeed(Long userId, Pageable pageable) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        // 팔로우한 사용자들의 ID 목록 조회
        List<Long> followingIds = followRepository.findFollowingIdsByUser(user);

        // 팔로우한 사용자가 없으면 ID 목록 조회
        if (followingIds.isEmpty()) {
            Page<PostResponse> emptyPage = Page.empty(pageable);
            return PostPageResponse.from(emptyPage);
        }

        Page<Post> posts = postRepository.findPostsByFollowingUsers(followingIds, pageable);
        Page<PostResponse> postResponses = posts.map(PostResponse::from);
        return PostPageResponse.from(postResponses);
    }

    // 특정 사용자의 게시물 조회
    public PostPageResponse getUserPosts(Long userId, Pageable pageable) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Page<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(user, pageable);
        Page<PostResponse> postResponses = posts.map(PostResponse::from);
        return PostPageResponse.from(postResponses);
    }

    @Transactional
    public PostResponse updatePost(Long userId, Long postId, PostUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        // 작성자 확인
        if (!post.isAuthor(user)) {
            throw new UnauthorizedException("게시물을 삭제할 권한이 없습니다");
        }

        post.update(request.getTitle(), request.getContent(), request.getImageUrl());
        return PostResponse.from(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        // 작성자 확인
        if (!post.isAuthor(user)) {
            throw new UnauthorizedException("게시물을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    // 제목으로 게시물 검색
    public PostPageResponse searchByTitle(String title, Pageable pageable) {
        Page<Post> posts = postRepository.findByTitleContainingOrderByCreatedAtDesc(title, pageable);
        Page<PostResponse> postResponses = posts.map(PostResponse::from);
        return PostPageResponse.from(postResponses);
    }

    // 내용으로 게시물 검색
    public PostPageResponse searchByContent(String content, Pageable pageable) {
        Page<Post> posts = postRepository.findByContentContainingOrderByCreatedAtDesc(content, pageable);
        Page<PostResponse> postResponses = posts.map(PostResponse::from);
        return PostPageResponse.from(postResponses);
    }

    // 기간별 게시물 검색
    public PostPageResponse searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Post> posts = postRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate, pageable);
        Page<PostResponse> postResponses = posts.map(PostResponse::from);
        return PostPageResponse.from(postResponses);
    }



}

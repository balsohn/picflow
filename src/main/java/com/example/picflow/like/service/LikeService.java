package com.example.picflow.like.service;

import com.example.picflow.global.exception.DuplicateException;
import com.example.picflow.global.exception.PostNotFoundException;
import com.example.picflow.global.exception.UserNotFoundException;
import com.example.picflow.like.dto.LikeResponse;
import com.example.picflow.like.entity.Like;
import com.example.picflow.like.repository.LikeRepository;
import com.example.picflow.post.entity.Post;
import com.example.picflow.post.repository.PostRepository;
import com.example.picflow.user.entity.User;
import com.example.picflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponse likePost(Long userId, Long postId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        // 자신의 게시물에는 좋아요를 누를 수 없음
        if (post.isAuthor(user)) {
            throw new IllegalArgumentException("자신의 게시물에는 좋아요를 누를 수 없습니다.");
        }

        // 이미 좋아요를 눌렀는지 확인
        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new DuplicateException("이미 좋아요를 누른 게시물입니다.");
        }

        Like like = Like.create(post, user);
        Like savedLike = likeRepository.save(like);

        // 게시물의 좋아요 수 증가
        post.increaseLikeCount();

        return LikeResponse.from(savedLike);
    }

    @Transactional
    public void unlikePost(Long userId, Long postId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Like like = likeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 게시물입니다."));

        likeRepository.delete(like);

        // 게시물의 좋아요 수 감소
        post.decreaseLikeCount();
    }

    // 게시물의 좋아요 목록 조회
    public List<LikeResponse> getLikesByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        List<Like> likes = likeRepository.findByPostOrderByCreatedAtDesc(post);
        return likes.stream()
                .map(LikeResponse::from)
                .collect(Collectors.toList());
    }

    // 좋아요 수 조회
    public long getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        return likeRepository.countByPost(post);
    }

    // 사용자가 특정 게시물에 좋아요를 눌렀는지 확인
    public boolean isLikedByUser(Long userId, Long postId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return likeRepository.existsByPostAndUser(post, user);
    }

    // 사용자가 좋아요한 게시물 목록 조회
    public List<LikeResponse> getLikesByUser(Long userId, Pageable pageable) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Page<Like> likes = likeRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return likes.stream()
                .map(LikeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLike(Long userId, Long likeId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좋아요입니다."));

        // 좋아요를 누른 사용자인지 확인
        if (!like.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("좋아요를 취소할 권한이 없습니다.");
        }

        likeRepository.delete(like);

        // 게시물의 좋아요 수 감소
        like.getPost().decreaseLikeCount();
    }
}
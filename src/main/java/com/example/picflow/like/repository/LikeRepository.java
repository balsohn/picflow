package com.example.picflow.like.repository;

import com.example.picflow.like.entity.Like;
import com.example.picflow.post.entity.Post;
import com.example.picflow.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 게시물과 사용자의 좋아요 관계 조회
    Optional<Like> findByPostAndUser(Post post, User user);

    // 특정 게시물과 사용자의 좋아요 관계 존재 여부 확인
    boolean existsByPostAndUser(Post post, User user);

    // 특정 게시물의 좋아요 목록 조회 (최신순)
    List<Like> findByPostOrderByCreatedAtDesc(Post post);

    // 특정 사용자가 좋아요한 목록 조회 (최신순)
    Page<Like> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // 특정 게시물의 좋아요 수 조회
    long countByPost(Post post);

    // 특정 사용자가 누른 좋아요 수 조회
    long countByUser(User user);
}
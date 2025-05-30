package com.example.picflow.post.repository;

import com.example.picflow.post.entity.Post;
import com.example.picflow.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 팔로우한 사용자들의 게시물 조회 (뉴스피드)
    @Query("SELECT p FROM Post p WHERE p.author.id IN :followingIds ORDER BY p.createdAt DESC")
    Page<Post> findPostsByFollowingUsers(@Param("followingIds") List<Long> followingIds, Pageable pageable);

    // 제목으로 게시물 검색
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:title% ORDER BY p.createdAt DESC")
    Page<Post> findByTitleContainingOrderByCreatedAtDesc(@Param("title") String title, Pageable pageable);

    // 내용으로 게시물 검색
    @Query("SELECT p FROM Post p WHERE p.content LIKE %:content% ORDER BY p.createdAt DESC")
    Page<Post> findByContentContainingOrderByCreatedAtDesc(@Param("content") String content, Pageable pageable);

    // 기간별 게시물 검색
    @Query("SELECT p FROM Post p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    Page<Post> findByCreatedAtBetweenOrderByCreatedAtDesc(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // 특정 사용자의 게시물 수 조회
    long countByAuthor(User author);
}

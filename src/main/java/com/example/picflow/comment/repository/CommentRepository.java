package com.example.picflow.comment.repository;

import com.example.picflow.comment.entity.Comment;
import com.example.picflow.post.entity.Post;
import com.example.picflow.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시물의 댓글 조회 (최신순)
    Page<Comment> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable);

    // 특정 사용자의 댓글 조회(최신순)
    Page<Comment> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    // 특정 게시물의 댓글 수 조회
    long countByPost(Post post);

    // 특정 사용자의 댓글 수 조회
    long countByAuthor(User author);
}

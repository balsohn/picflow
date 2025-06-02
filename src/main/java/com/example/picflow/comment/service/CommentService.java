package com.example.picflow.comment.service;

import com.example.picflow.comment.dto.CommentCreateRequest;
import com.example.picflow.comment.dto.CommentPageResponse;
import com.example.picflow.comment.dto.CommentResponse;
import com.example.picflow.comment.dto.CommentUpdateRequest;
import com.example.picflow.comment.entity.Comment;
import com.example.picflow.comment.repository.CommentRepository;
import com.example.picflow.global.exception.CommentNotFoundException;
import com.example.picflow.global.exception.PostNotFoundException;
import com.example.picflow.global.exception.UnauthorizedException;
import com.example.picflow.global.exception.UserNotFoundException;
import com.example.picflow.post.entity.Post;
import com.example.picflow.post.repository.PostRepository;
import com.example.picflow.user.entity.User;
import com.example.picflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(Long userId, Long postId, CommentCreateRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Comment comment = Comment.create(request.getContent(), post, user);
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.from(savedComment);
    }

    public CommentPageResponse getCommentsByPost(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Page<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post, pageable);
        Page<CommentResponse> commentResponses = comments.map(CommentResponse::from);

        return CommentPageResponse.from(commentResponses);
    }

    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, CommentUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        // 댓글 작성자이거나 게시물 작성자인지 확인
        if (!comment.isAuthor(user) && !comment.getPost().isAuthor(user)) {
            throw new UnauthorizedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.update(request.getContent());
        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        // 댓글 작성자이거나 게시물 작성자인지 확인
        if (!comment.isAuthor(user) && !comment.getPost().isAuthor(user)) {
            throw new UnauthorizedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    // 특정 사용자의 댓글 조회
    public CommentPageResponse getCommentsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Page<Comment> comments = commentRepository.findByAuthorOrderByCreatedAtDesc(user, pageable);
        Page<CommentResponse> commentResponses = comments.map(CommentResponse::from);

        return CommentPageResponse.from(commentResponses);
    }

    // 댓글 수 조회
    public long getCommentCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        return commentRepository.countByPost(post);
    }
}
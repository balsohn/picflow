package com.example.picflow.user.repository;

import com.example.picflow.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndDeleteFalse(String email);
    Optional<User> findByIdAndDeletedFalse(Long id);
    boolean existsByEmail(String email);
    boolean existsByEmailAndDeletedFalse(String email);

    // 사용자명으로 검색 (활성 사용자만, 페이징)
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username% AND u.deleted = false")
    Page<User> findByUsernameContainingAndDeletedFalse(@Param("username") String username, Pageable pageable);

    // 이메일로 검색 (활성 사용자만, 페이징)
    @Query("SELECT u FROM User u WHERE u.email LIKE %:email% AND u.deleted = false")
    Page<User> findByEmailContainingAndDeletedFalse(@Param("email") String email, Pageable pageable);

    // 모든 활성 사용자 조회 (페이징)
    Page<User> findByDeletedFalse(Pageable pageable);
}

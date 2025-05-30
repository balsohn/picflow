package com.example.picflow.follow.repository;

import com.example.picflow.follow.entity.Follow;
import com.example.picflow.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    // 팔로우 관계 조회
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // 팔로우 관계 존재 여부 확인
    boolean existsByFollowerAndFollowing(User follower, User following);

    // 특정 사용자가 팔로우하는 사람들 조회 (팔로잉 목록)
    @Query("SELECT f.following FROM Follow f WHERE f.follower = :user ORDER BY f.createdAt DESC")
    Page<User> findFollowingByUser(@Param("user") User user, Pageable pageable);

    // 특정 사용자를 팔로우하는 사람들 조회 (팔로워 목록)
    @Query("SELECT f.follower FROM Follow f WHERE f.following = :user ORDER BY f.createdAt DESC")
    Page<User> findFollowersByUser(@Param("user") User user, Pageable pageable);

    // 팔로잉 수 조회
    long countByFollower(User follower);

    // 팔로워 수 조회
    long countByFollowing(User following);

    // 특정 사용자가 팔로우하는 사용자들의 ID 목록 조회 (뉴스피드용)
    @Query("SELECT f.following.id FROM Follow f WHERE f.follower = :user")
    List<Long> findFollowingIdsByUser(@Param("user") User user);

    // 특정 사용자를 팔로우하는 Follow 엔티티들 조회
    List<Follow> findByFollowing(User following);

    // 특정 사용자가 팔로우하는 Follow 엔티티들 조회
    List<Follow> findByFollower(User follower);
}

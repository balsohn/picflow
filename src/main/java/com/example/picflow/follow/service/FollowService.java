package com.example.picflow.follow.service;

import com.example.picflow.follow.dto.FollowPageResponse;
import com.example.picflow.follow.dto.FollowResponse;
import com.example.picflow.follow.entity.Follow;
import com.example.picflow.follow.repository.FollowRepository;
import com.example.picflow.global.exception.DuplicateException;
import com.example.picflow.global.exception.FollowNotFoundException;
import com.example.picflow.global.exception.UserNotFoundException;
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
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(Long followerId, Long followingId) {
        // 자기자신을 팔로우하는지 확인
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        User follower = userRepository.findByIdAndDeletedFalse(followerId)
                .orElseThrow(UserNotFoundException::new);

        User following = userRepository.findByIdAndDeletedFalse(followingId)
                .orElseThrow(() -> new UserNotFoundException("팔로우하려는 사용자가 존재하지 않습니다."));

        // 이미 팔로우 중인지 확인
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new DuplicateException("이미 팔로우한 사용자입니다.");
        }

        Follow follow = Follow.create(follower, following);
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        User follower = userRepository.findByIdAndDeletedFalse(followerId)
                .orElseThrow(UserNotFoundException::new);

        User following = userRepository.findByIdAndDeletedFalse(followingId)
                .orElseThrow(() -> new UserNotFoundException("언팔로우 하려는 사용자가 존재하지 않습니다."));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new FollowNotFoundException("팔로우 관계가 존재하지 않습니다."));

        followRepository.delete(follow);
    }

    // 팔로잉목록 조회 (내가 팔로우 하는 사람들)
    public FollowPageResponse getFollowing(Long userId, Pageable pageable) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Page<User> followingUsers = followRepository.findFollowingByUser(user, pageable);

        // Follow 엔티티에서 createAt을 가져오기 위해 별도 처리
        Page<FollowResponse> followResponses = followingUsers.map(followingUser -> {
            Follow follow = followRepository.findByFollowerAndFollowing(user, followingUser)
                    .orElse(null);
            return FollowResponse.from(followingUser,
                    follow != null ? follow.getCreatedAt() : null);
        });

        return FollowPageResponse.from(followResponses);
    }

    // 팔로워 목록 조회 (나를 팔로우하는 사람들)
    public FollowPageResponse getFollowers(Long userId, Pageable pageable) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        Page<User> followerUsers = followRepository.findFollowersByUser(user, pageable);

        // Follow 엔티티에서 createdAt을 가져오기 위해 별도 처리
        Page<FollowResponse> followResponses = followerUsers.map(followerUser -> {
            Follow follow = followRepository.findByFollowerAndFollowing(followerUser, user)
                    .orElse(null);
            return FollowResponse.from(followerUser,
                    follow != null ? follow.getCreatedAt() : null);
        });

        return FollowPageResponse.from(followResponses);
    }

    // 팔로잉 수 조회
    public long getFollowingCount(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);
        return followRepository.countByFollower(user);
    }

    // 팔로워 수 조회
    public long getFollowersCount(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);
        return followRepository.countByFollowing(user);
    }

    // 팔로우 관계 확인
    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            return false; // 자기 자신은 팔로우 관계가 아님
        }

        User follower = userRepository.findByIdAndDeletedFalse(followerId)
                .orElseThrow(UserNotFoundException::new);

        User following = userRepository.findByIdAndDeletedFalse(followingId)
                .orElseThrow(UserNotFoundException::new);

        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
}

package org.pawly.pawlybackend.service;

import lombok.RequiredArgsConstructor;
import org.pawly.pawlybackend.dto.UserProfileResponse;
import org.pawly.pawlybackend.entity.Follow;
import org.pawly.pawlybackend.entity.User;
import org.pawly.pawlybackend.exception.ResourceNotFoundException;
import org.pawly.pawlybackend.repository.FollowRepository;
import org.pawly.pawlybackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public UserProfileResponse getUserProfile(String username, UUID currentUserId) {
        User targetUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfileResponse response = new UserProfileResponse();
        response.setId(targetUser.getId());
        response.setUsername(targetUser.getUsername());
        response.setBio(targetUser.getBio());
        response.setProfilePictureUrl(targetUser.getProfilePictureUrl());
        response.setCreatedAt(targetUser.getCreatedAt());
        response.setFollowerCount(followRepository.countByFollowingId(targetUser.getId()));
        response.setFollowingCount(followRepository.countByFollowerId(targetUser.getId()));

        if (currentUserId != null) {
            response.setFollowing(followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUser.getId()));
        } else {
            response.setFollowing(false);
        }

        return response;
    }

    public void followUser(UUID targetUserId, UUID currentUserId) {
        if (targetUserId.equals(currentUserId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        Optional<Follow> existingFollow = followRepository.findByFollowerIdAndFollowingId(currentUserId, targetUserId);
        if (existingFollow.isEmpty()) {
            Follow follow = new Follow();
            follow.setFollower(currentUser);
            follow.setFollowing(targetUser);
            followRepository.save(follow);
        }
    }

    public void unfollowUser(UUID targetUserId, UUID currentUserId) {
        Optional<Follow> existingFollow = followRepository.findByFollowerIdAndFollowingId(currentUserId, targetUserId);
        existingFollow.ifPresent(followRepository::delete);
    }
}

package com.scv.domain.user.service;

import com.scv.domain.user.domain.User;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.user.exception.UserNotFoundException;
import com.scv.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::getInstance);
        return UserProfileResponseDTO.builder()
                .userEmail(user.getUserEmail())
                .userImageUrl(user.getUserImageUrl())
                .userNickname(user.getUserNickname())
                .build();
    }
}

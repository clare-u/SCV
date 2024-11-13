package com.scv.domain.user.service;

import com.scv.global.jwt.service.RedisTokenService;
import com.scv.global.jwt.util.JwtUtil;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.user.exception.UserNotFoundException;
import com.scv.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final RedisTokenService redisTokenService;
    private final UserRepository userRepository;

    // 유저 프로필 조회 서비스 로직
    @Override
    public UserProfileResponseDTO getUserProfile(CustomOAuth2User authUser) {
        User user = userRepository.findById(authUser.getUserId()).orElseThrow(UserNotFoundException::getInstance);
        return UserProfileResponseDTO.builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userImageUrl(user.getUserImageUrl())
                .userNickname(user.getUserNickname())
                .userRepo(user.getUserRepo())
                .build();
    }

    // 깃허브 리포 연동 해제 서비스 로직
    @Transactional
    @Override
    public String unLinkGithubRepo(CustomOAuth2User authUser, String accessToken) {
        userRepository.updateUserRepoById(authUser.getUserId(), null);

        redisTokenService.addToBlacklist(accessToken);

        return JwtUtil.createAccessToken(userRepository.findById(authUser.getUserId()).orElseThrow(UserNotFoundException::getInstance));
    }

}

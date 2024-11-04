package com.scv.domain.user.service;

import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.dto.request.CreateGithubRepositoryRequestDTO;
import com.scv.domain.user.dto.response.CommonSuccessResponseDTO;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.user.exception.DuplicateRepositoryNameException;
import com.scv.domain.user.exception.UserNotFoundException;
import com.scv.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final GithubService githubService;

    private final UserRepository userRepository;

    public UserProfileResponseDTO getUserProfile(CustomOAuth2User user2, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::getInstance);
        return UserProfileResponseDTO.builder()
                .userEmail(user.getUserEmail())
                .userImageUrl(user.getUserImageUrl())
                .userNickname(user.getUserNickname())
                .userRepo(user.getUserRepo())
                .build();
    }

    public CommonSuccessResponseDTO createGithubRepository(CustomOAuth2User user, CreateGithubRepositoryRequestDTO requestDTO) {
        if (githubService.getGithubRepositoryNames(user).contains(requestDTO.getRepoName())) {
            throw DuplicateRepositoryNameException.getInstance();
        }

        boolean responseStatus = githubService.createGithubRepository(user, requestDTO);
        return CommonSuccessResponseDTO.builder()
                .message(responseStatus ? "Success" : "Failed")
                .build();
    }

}

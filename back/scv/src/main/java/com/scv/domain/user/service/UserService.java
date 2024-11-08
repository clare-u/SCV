package com.scv.domain.user.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.github.service.GithubService;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.dto.request.CommitGithubRepositoryFileRequestDTO;
import com.scv.domain.user.dto.request.GithubRepositoryNameRequestDTO;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
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

    // 유저 프로필 조회 서비스 로직
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

    // 깃허브 새 리포를 메인 리포로 설정 서비스 로직
    public boolean setNewGithubRepository(CustomOAuth2User authUser, GithubRepositoryNameRequestDTO requestDTO) {
        if (existRepoName(authUser, requestDTO.getRepoName())) {
            return false;
        }

        if (!githubService.createGithubRepository(authUser, requestDTO)) {
            return false;
        }

        userRepository.updateUserRepoById(authUser.getUserId(), requestDTO.getRepoName());

        return true;
    }

    // 깃허브 기존 리포를 메인 리포로 설정 서비스 로직
    public boolean setCurrentGithubRepository(CustomOAuth2User authUser, GithubRepositoryNameRequestDTO requestDTO) {
        if (!existRepoName(authUser, requestDTO.getRepoName())) {
            return false;
        }

        userRepository.updateUserRepoById(authUser.getUserId(), requestDTO.getRepoName());

        return true;
    }

    public void disConnectGithubRepository(CustomOAuth2User authUser) {
        userRepository.updateUserRepoById(authUser.getUserId(), null);
    }

    public String importModel(CustomOAuth2User auth2User, String modelName) {
        return githubService.getGithubRepositoryFile(auth2User, modelName);
    }

    public boolean exportModel(CustomOAuth2User auth2User, CommitGithubRepositoryFileRequestDTO requestDTO) {
        return githubService.commitGithubRepositoryFile(auth2User, requestDTO);
    }

    private boolean existRepoName(CustomOAuth2User authUser, String repoName) {
        return githubService.getGithubRepositoryNames(authUser).contains(repoName);
    }
}

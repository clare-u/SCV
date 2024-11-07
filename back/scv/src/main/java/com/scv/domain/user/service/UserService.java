package com.scv.domain.user.service;

import com.scv.domain.github.service.GithubService;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.dto.request.CommitGithubRepositoryFileRequestDTO;
import com.scv.domain.user.dto.request.GithubRepositoryNameRequestDTO;
import com.scv.domain.user.dto.response.GithubRepositoryNameResponseDTO;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.user.exception.DuplicateRepositoryException;
import com.scv.domain.user.exception.InternalServerErrorException;
import com.scv.domain.user.exception.RepositoryNotFoundException;
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

    public GithubRepositoryNameResponseDTO setNewGithubRepository(CustomOAuth2User authUser, GithubRepositoryNameRequestDTO requestDTO) {
        if (githubService.getGithubRepositoryNames(authUser).contains(requestDTO.getRepoName())) {
            throw DuplicateRepositoryException.getInstance();
        }

        String newRepoName = githubService.createGithubRepository(authUser, requestDTO);

        if (newRepoName == null) {
            throw InternalServerErrorException.getInstance();
        }

        userRepository.updateUserRepoById(authUser.getUserId(), requestDTO.getRepoName());

        return GithubRepositoryNameResponseDTO.builder()
                .repoName(requestDTO.getRepoName())
                .build();
    }

    public GithubRepositoryNameResponseDTO setCurrentGithubRepository(CustomOAuth2User authUser, GithubRepositoryNameRequestDTO requestDTO) {
        if (!githubService.getGithubRepositoryNames(authUser).contains(requestDTO.getRepoName())) {
            throw RepositoryNotFoundException.getInstance();
        }

        userRepository.updateUserRepoById(authUser.getUserId(), requestDTO.getRepoName());

        return GithubRepositoryNameResponseDTO.builder()
                .repoName(requestDTO.getRepoName())
                .build();
    }

    public String importModel(CustomOAuth2User auth2User, String modelName) {
        return githubService.getGithubRepositoryFile(auth2User, modelName);
    }

    public String exportModel(CustomOAuth2User auth2User, CommitGithubRepositoryFileRequestDTO requestDTO) {
        return githubService.commitGithubRepositoryFile(auth2User, requestDTO);
    }
}

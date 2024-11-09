package com.scv.domain.user.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.user.dto.request.LinkGithubRepoRequestDTO;
import com.scv.domain.user.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoApiResponseDTO;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.request.CommitGithubRepoFileRequestDTO;
import com.scv.domain.user.dto.response.GithubRepoFileResponseDTO;
import com.scv.domain.user.exception.GithubConflictException;
import com.scv.domain.user.exception.GithubNotFoundException;
import com.scv.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GithubService {

    private final UserRepository userRepository;

    private final GithubApiService githubApiService;

    // 깃허브에서 primary email 을 조회하는 메서드
    public String getGithubPrimaryEmail(String accessToken) {
        return githubApiService.getGithubEmailList(accessToken).stream()
                .filter(GithubEmailApiResponseDTO::getPrimary)
                .map(GithubEmailApiResponseDTO::getEmail)
                .findFirst()
                .orElse(null);
    }

    // 깃허브 새 리포를 메인 리포로 설정 서비스 로직
    public void linkNewGithubRepo(CustomOAuth2User authUser, LinkGithubRepoRequestDTO requestDTO) {
        if (getGithubRepoNames(authUser).contains(requestDTO.getRepoName())) {
            throw GithubConflictException.getInstance();
        }

        githubApiService.createGithubRepo(authUser, requestDTO);

        userRepository.updateUserRepoById(authUser.getUserId(), requestDTO.getRepoName());
    }

    // 깃허브 기존 리포를 메인 리포로 설정 서비스 로직
    public void linkCurrentGithubRepo(CustomOAuth2User authUser, LinkGithubRepoRequestDTO requestDTO) {
        if (!getGithubRepoNames(authUser).contains(requestDTO.getRepoName())) {
            throw GithubNotFoundException.getInstance();
        }

        userRepository.updateUserRepoById(authUser.getUserId(), requestDTO.getRepoName());
    }

    // 깃허브에서 Repository 리스트의 이름들을 Set 으로 반환
    private Set<String> getGithubRepoNames(CustomOAuth2User authUser) {
        return githubApiService.getGithubRepoList(authUser).stream()
                .map(GithubRepoApiResponseDTO::getName)
                .collect(Collectors.toSet());
    }

    // 깃허브에서 모델 import 서비스 로직
    public GithubRepoFileResponseDTO importGithubRepoFile(CustomOAuth2User auth2User, DataSet dataName, String modelName) {
        return GithubRepoFileResponseDTO.builder()
                .content(githubApiService.getContentFromGithubRepositoryFile(auth2User, dataName, modelName))
                .build();
    }

    // 깃허브에 모델 export 서비스 로직
    public void exportGithubRepoFile(CustomOAuth2User auth2User, CommitGithubRepoFileRequestDTO requestDTO) {
        githubApiService.commitGithubRepositoryFile(auth2User, requestDTO);
    }

}

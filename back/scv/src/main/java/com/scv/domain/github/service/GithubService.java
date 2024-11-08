package com.scv.domain.github.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.github.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.github.dto.response.GithubRepositoryApiResponseDTO;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.request.CommitGithubRepositoryFileRequestDTO;
import com.scv.domain.user.dto.request.GithubRepositoryNameRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final GithubApiService githubApiService;

    // Github 에서 primary email 을 조회하는 메서드
    public String getGithubPrimaryEmail(String accessToken) {
        return githubApiService.getGithubEmailList(accessToken).stream()
                .filter(GithubEmailApiResponseDTO::getPrimary)
                .map(GithubEmailApiResponseDTO::getEmail)
                .findFirst()
                .orElse(null);
    }

    // Github 에서 Repository 리스트의 이름들을 Set 으로 반환
    public Set<String> getGithubRepositoryNames(CustomOAuth2User authUser) {
        return githubApiService.getGithubRepositoryList(authUser).stream()
                .map(GithubRepositoryApiResponseDTO::getName)
                .collect(Collectors.toSet());
    }

    // Github 에 Repository 생성하는 메서드
    public boolean createGithubRepository(CustomOAuth2User authUser, GithubRepositoryNameRequestDTO requestDTO) {
        return githubApiService.createGithubRepository(authUser, requestDTO);
    }

    // Github 에 파일을 커밋하는 메서드
    public boolean commitGithubRepositoryFile(CustomOAuth2User authUser, CommitGithubRepositoryFileRequestDTO requestDTO) {
        return githubApiService.getShaFromGithubRepositoryFile(authUser, requestDTO)
                .map(sha -> githubApiService.updateGithubRepositoryFile(authUser, requestDTO, sha))
                .orElseGet(() -> githubApiService.commitGithubRepositoryFile(authUser, requestDTO));
    }

    // Github 에서 파일에서 내용을 가져오는 메서드
    public String getGithubRepositoryFile(CustomOAuth2User authUser, DataSet dataName, String modelName) {
        return githubApiService.getContentFromGithubRepositoryFile(authUser, dataName, modelName).orElse(null);
    }

}

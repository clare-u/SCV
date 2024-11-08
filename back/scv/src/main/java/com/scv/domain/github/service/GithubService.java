package com.scv.domain.github.service;

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
        System.out.println(githubApiService.getGithubRepositoryList(authUser).stream()
                .map(GithubRepositoryApiResponseDTO::getName)
                .collect(Collectors.toSet()));
        return githubApiService.getGithubRepositoryList(authUser).stream()
                .map(GithubRepositoryApiResponseDTO::getName)
                .collect(Collectors.toSet());
    }

    // Github 에 Repository 생성하는 메서드
    public String createGithubRepository(CustomOAuth2User authUser, GithubRepositoryNameRequestDTO requestDTO) {
        return githubApiService.createGithubRepository(authUser, requestDTO.getRepoName());
    }

    // Github 에 파일을 커밋하는 메서드
    public String commitGithubRepositoryFile(CustomOAuth2User authUser, CommitGithubRepositoryFileRequestDTO requestDTO) {
        System.out.println("111");
        String sha = githubApiService.getShaFromGithubRepositoryFile(authUser, requestDTO.getPath());
        System.out.println("222");
        if (sha == null) {
            System.out.println("333");
            return githubApiService.commitGithubRepositoryFile(authUser, requestDTO);
        } else {
            System.out.println("444");
            return githubApiService.updateGithubRepositoryFile(authUser, requestDTO, sha);
        }
    }

    // Github 에서 파일에서 내용을 가져오는 메서드
    public String getGithubRepositoryFile(CustomOAuth2User authUser, String path) {
        return githubApiService.getContentFromGithubRepositoryFile(authUser, path);
    }

}

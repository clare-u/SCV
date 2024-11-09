package com.scv.domain.user.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.user.dto.request.CommitGithubRepoFileApiRequestDTO;
import com.scv.domain.user.dto.request.CreateGithubRepoApiRequestDTO;
import com.scv.domain.user.dto.request.LinkGithubRepoRequestDTO;
import com.scv.domain.user.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoFileApiResponseDTO;
import com.scv.domain.user.exception.GithubNotFoundException;
import com.scv.domain.user.util.GithubUrlBuilder;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.request.CommitGithubRepoFileRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GithubApiService {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    private final GithubUrlBuilder githubUrlBuilder;

    private final RestTemplate restTemplate;

    // 인증된 유저의 username 으로 AccessToken 반환
    private String getAccessToken(String username) {
        return oAuth2AuthorizedClientService.loadAuthorizedClient("github", username)
                .getAccessToken()
                .getTokenValue();
    }

    // Github REST API 용 Header 반환
    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github+json")));
        return headers;
    }

    // Github REST API 용 Header 반환
    private HttpHeaders createHeaders(CustomOAuth2User authUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken(authUser.getName()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github+json")));
        return headers;
    }

    // Github 에서 email 목록을 조회하는 메서드
    // https://api.github.com/user/emails
    // https://docs.github.com/ko/rest/users/emails?apiVersion=2022-11-28#list-email-addresses-for-the-authenticated-user
    public List<GithubEmailApiResponseDTO> getGithubEmailList(String accessToken) {
        String url = githubUrlBuilder.buildEmailListUrl();

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders(accessToken));
        ResponseEntity<List<GithubEmailApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }

    // Github 에 Repository 를 생성하는 메서드
    // https://api.github.com/user/repos
    // https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#create-a-repository-for-the-authenticated-user
    public void createGithubRepo(CustomOAuth2User authUser, LinkGithubRepoRequestDTO requestDTO) {
        String url = githubUrlBuilder.buildCreateRepoUrl();

        CreateGithubRepoApiRequestDTO requestBody = CreateGithubRepoApiRequestDTO.builder()
                .name(requestDTO.getRepoName())
                .description("Welcome " + requestDTO.getRepoName() + "!")
                .build();

        HttpEntity<CreateGithubRepoApiRequestDTO> entity = new HttpEntity<>(requestBody, createHeaders(authUser));
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }

    // Github 에서 Repository 목록을 조회하는 메서드
    // https://api.github.com/users/{userNickname}/repos
    // https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
    public List<GithubRepoApiResponseDTO> getGithubRepoList(CustomOAuth2User authUser) {
        String url = githubUrlBuilder.buildRepoListUrl(authUser);

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders(authUser));
        ResponseEntity<List<GithubRepoApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }

    // Github 에서 블록 파일을 가져오는 메서드
    // https://api.github.com/repos/{userNickname}/{userRepo}/contents/{dataName}/{modelName}/block.json
    // https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#get-repository-content
    private Optional<GithubRepoFileApiResponseDTO> getGithubRepoFile(CustomOAuth2User authUser, DataSet dataName, String modelName) {
        String url = githubUrlBuilder.buildRepoFileUrl(authUser, dataName, modelName);

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders(authUser));
        try {
            ResponseEntity<GithubRepoFileApiResponseDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
            return Optional.ofNullable(responseEntity.getBody());
        } catch (GithubNotFoundException e) {
            return Optional.empty();
        }
    }

    // Github 파일에서 sha 를 가져오는 메서드
    private String getShaFromGithubRepositoryFile(CustomOAuth2User authUser, CommitGithubRepoFileRequestDTO requestDTO) {
        return getGithubRepoFile(authUser, requestDTO.getDataName(), requestDTO.getModelName())
                .map(GithubRepoFileApiResponseDTO::getSha)
                .orElse(null);
    }

    // Github 파일에서 content 를 가져오는 메서드
    public String getContentFromGithubRepositoryFile(CustomOAuth2User authUser, DataSet dataName, String modelName) {
        return getGithubRepoFile(authUser, dataName, modelName)
                .map(GithubRepoFileApiResponseDTO::getContent)
                .map(content -> new String(Base64.getDecoder().decode(content.replaceAll("\\s", ""))))
                .orElseThrow(GithubNotFoundException::getInstance);
    }

    // Github 에 파일을 커밋하는 메서드
    // https://api.github.com/repos/{userNickname}/{userRepo}/contents/{dataName}/{modelName}/block.json
    // https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents
    public void commitGithubRepositoryFile(CustomOAuth2User authUser, CommitGithubRepoFileRequestDTO requestDTO) {
        String url = githubUrlBuilder.buildRepoFileUrl(authUser, requestDTO.getDataName(), requestDTO.getModelName());

        String sha = getShaFromGithubRepositoryFile(authUser, requestDTO);
        String message = "";

        if (sha == null) {
            message = "feat: [" + requestDTO.getModelName() + " v-" + requestDTO.getVersionNo() + "] - SCV";
        } else {
            message = "refactor: [" + requestDTO.getModelName() + " v-" + requestDTO.getVersionNo() + "] - SCV";
        }

        CommitGithubRepoFileApiRequestDTO requestBody = CommitGithubRepoFileApiRequestDTO.builder()
                .message(message)
                .content(Base64.getEncoder().encodeToString(requestDTO.getContent().getBytes()))
                .sha(sha)
                .build();

        HttpEntity<CommitGithubRepoFileApiRequestDTO> entity = new HttpEntity<>(requestBody, createHeaders(authUser));
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

}

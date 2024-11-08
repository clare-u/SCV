package com.scv.domain.github.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.github.dto.request.CommitGithubRepositoryFileApiRequestDTO;
import com.scv.domain.github.dto.request.CreateGithubRepositoryApiRequestDTO;
import com.scv.domain.github.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.github.dto.response.GithubRepositoryApiResponseDTO;
import com.scv.domain.github.dto.response.GithubRepositoryFileApiResponseDTO;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.request.CommitGithubRepositoryFileRequestDTO;
import com.scv.domain.user.dto.request.GithubRepositoryNameRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GithubApiService {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    private final RestTemplate restTemplate;

    private static final String GITHUB_API_URL = "https://api.github.com";

    // 인증된 유저의 username 으로 AccessToken 반환
    private String getAccessToken(String username) {
        return oAuth2AuthorizedClientService.loadAuthorizedClient("github", username)
                .getAccessToken()
                .getTokenValue();
    }

    // AccessToken 을 포함한 GitHub REST API 용 Header 반환
    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github+json")));
        return headers;
    }

    // Github 에서 email 리스트를 조회하는 메서드
    // https://docs.github.com/ko/rest/users/emails?apiVersion=2022-11-28#list-email-addresses-for-the-authenticated-user
    public List<GithubEmailApiResponseDTO> getGithubEmailList(String accessToken) {
        String url = GITHUB_API_URL + "/user/emails";

        HttpEntity<String> entity = new HttpEntity<>(createHeaders(accessToken));
        try {
            ResponseEntity<List<GithubEmailApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return Collections.emptyList();
        }
    }

    // Github 에서 Repository 리스트를 조회하는 메서드
    // https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
    public List<GithubRepositoryApiResponseDTO> getGithubRepositoryList(CustomOAuth2User authUser) {
        String url = GITHUB_API_URL + "/users/" + authUser.getUserNickname() + "/repos";

        HttpEntity<String> entity = new HttpEntity<>(createHeaders(getAccessToken(authUser.getName())));
        try {
            ResponseEntity<List<GithubRepositoryApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return Collections.emptyList();
        }
    }

    // Github 에 Repository 를 생성하는 메서드
    // https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#create-a-repository-for-the-authenticated-user
    public boolean createGithubRepository(CustomOAuth2User authUser, GithubRepositoryNameRequestDTO requestDTO) {
        String url = GITHUB_API_URL + "/user/repos";

        CreateGithubRepositoryApiRequestDTO requestBody = CreateGithubRepositoryApiRequestDTO.builder()
                .name(requestDTO.getRepoName())
                .description("Welcome " + requestDTO.getRepoName() + "!")
                .build();

        HttpEntity<CreateGithubRepositoryApiRequestDTO> entity = new HttpEntity<>(requestBody, createHeaders(getAccessToken(authUser.getName())));
        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            return true;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return false;
        }
    }

    // Github 에서 파일을 가져오는 메서드
    // https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#get-repository-content
    private Optional<GithubRepositoryFileApiResponseDTO> getGithubRepositoryFile(CustomOAuth2User authUser, DataSet dataName, String modelName) {
        String url = GITHUB_API_URL + "/repos/" + authUser.getUserNickname() + "/" + authUser.getUserRepo() + "/contents/" + dataName + "/" + modelName + "/block.json";

        HttpEntity<GithubRepositoryFileApiResponseDTO> entity = new HttpEntity<>(createHeaders(getAccessToken(authUser.getName())));
        try {
            ResponseEntity<GithubRepositoryFileApiResponseDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
            return Optional.ofNullable(responseEntity.getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Github 에서 파일에서 sha 를 가져오는 메서드
    public Optional<String> getShaFromGithubRepositoryFile(CustomOAuth2User authUser, CommitGithubRepositoryFileRequestDTO requestDTO) {
        return getGithubRepositoryFile(authUser, requestDTO.getDataName(), requestDTO.getModelName())
                .map(GithubRepositoryFileApiResponseDTO::getSha);
    }

    // Github 에서 파일에서 content 를 가져오는 메서드
    public Optional<String> getContentFromGithubRepositoryFile(CustomOAuth2User authUser, DataSet dataName, String modelName) {
        return getGithubRepositoryFile(authUser, dataName, modelName)
                .map(GithubRepositoryFileApiResponseDTO::getContent)
                .map(content -> content.replaceAll("\\s", ""))
                .map(content -> new String(Base64.getDecoder().decode(content)));
    }

    // Github 에 파일을 커밋하는 메서드
    // https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents
    public boolean commitGithubRepositoryFile(CustomOAuth2User authUser, CommitGithubRepositoryFileRequestDTO requestDTO) {
        String url = GITHUB_API_URL + "/repos/" + authUser.getUserNickname() + "/" + authUser.getUserRepo() + "/contents/" + requestDTO.getDataName() + "/" + requestDTO.getModelName() + "/block.json";

        CommitGithubRepositoryFileApiRequestDTO requestBody = CommitGithubRepositoryFileApiRequestDTO.builder()
                .message("feat: [" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] version-" + requestDTO.getVersionNo())
                .content(Base64.getEncoder().encodeToString(requestDTO.getContent().getBytes()))
                .build();

        HttpEntity<CommitGithubRepositoryFileApiRequestDTO> entity = new HttpEntity<>(requestBody, createHeaders(getAccessToken(authUser.getName())));

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            return true;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return false;
        }
    }

    // Github 에 파일을 커밋하는 메서드
    // https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents
    public boolean updateGithubRepositoryFile(CustomOAuth2User authUser, CommitGithubRepositoryFileRequestDTO requestDTO, String sha) {
        String url = GITHUB_API_URL + "/repos/" + authUser.getUserNickname() + "/" + authUser.getUserRepo() + "/contents/" + requestDTO.getDataName() + "/" + requestDTO.getModelName() + "/block.json";

        CommitGithubRepositoryFileApiRequestDTO requestBody = CommitGithubRepositoryFileApiRequestDTO.builder()
                .message("refactor: [" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] version-" + requestDTO.getVersionNo())
                .content(Base64.getEncoder().encodeToString(requestDTO.getContent().getBytes()))
                .sha(sha)
                .build();

        HttpEntity<CommitGithubRepositoryFileApiRequestDTO> entity = new HttpEntity<>(requestBody, createHeaders(getAccessToken(authUser.getName())));

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            return true;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return false;
        }
    }

}

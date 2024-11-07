package com.scv.domain.github.service;

import com.scv.domain.github.dto.request.CommitGithubRepositoryFileApiRequestDTO;
import com.scv.domain.github.dto.request.CreateGithubRepositoryApiRequestDTO;
import com.scv.domain.github.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.github.dto.response.GithubRepositoryApiResponseDTO;
import com.scv.domain.github.dto.response.GithubRepositoryFileApiResponseDTO;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.request.CommitGithubRepositoryFileRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

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
        ResponseEntity<List<GithubEmailApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            return Collections.emptyList();
        }

        return responseEntity.getBody();
    }

    // Github 에서 Repository 리스트를 조회하는 메서드
    // https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
    public List<GithubRepositoryApiResponseDTO> getGithubRepositoryList(CustomOAuth2User authUser) {
        String url = GITHUB_API_URL + "/users/" + authUser.getUserNickname() + "/repos";

        HttpEntity<String> entity = new HttpEntity<>(createHeaders(getAccessToken(authUser.getName())));
        ResponseEntity<List<GithubRepositoryApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            return Collections.emptyList();
        }

        return responseEntity.getBody();
    }

    // Github 에 Repository 를 생성하는 메서드
    // https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#create-a-repository-for-the-authenticated-user
    public String createGithubRepository(CustomOAuth2User authUser, String repoName) {
        String url = GITHUB_API_URL + "/user/repos";

        CreateGithubRepositoryApiRequestDTO requestBody = CreateGithubRepositoryApiRequestDTO.builder()
                .name(repoName)
                .description("Welcome " + repoName + "!")
                .build();

        HttpEntity<CreateGithubRepositoryApiRequestDTO> entity = new HttpEntity<>(requestBody, createHeaders(getAccessToken(authUser.getName())));
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        return responseEntity.getBody();
    }

    // Github 에서 파일에서 sha 를 가져오는 메서드
    public String getShaFromGithubRepositoryFile(CustomOAuth2User user, String path) {
        GithubRepositoryFileApiResponseDTO githubRepositoryFile = getGithubRepositoryFile(user, path);
        System.out.println("555");
        if (githubRepositoryFile == null) {
            System.out.println("666");
            return null;
        }
        System.out.println("777");
        return githubRepositoryFile.getSha();
    }

    // Github 에서 파일에서 content 를 가져오는 메서드
    public String getContentFromGithubRepositoryFile(CustomOAuth2User user, String path) {
        GithubRepositoryFileApiResponseDTO githubRepositoryFile = getGithubRepositoryFile(user, path);
        if (githubRepositoryFile == null) {
            return null;
        }
        return new String(Base64.getDecoder().decode(githubRepositoryFile.getContent()));
    }

    // Github 에 파일을 커밋하는 메서드
    // https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents
    public String commitGithubRepositoryFile(CustomOAuth2User authUser, CommitGithubRepositoryFileRequestDTO requestDTO) {
        System.out.println("aaa");
        String url = GITHUB_API_URL + "/repos/" + authUser.getUserNickname() + "/" + authUser.getUserRepo() + "/contents/" + requestDTO.getPath();
        System.out.println("url = " + url);
        CommitGithubRepositoryFileApiRequestDTO requestBody = CommitGithubRepositoryFileApiRequestDTO.builder()
                .message("feat: [" + LocalDateTime.now() + "] " + requestDTO.getPath())
                .content(Base64.getEncoder().encodeToString(requestDTO.getContent().getBytes()))
                .build();
        System.out.println("requestBody = " + requestBody);
        HttpEntity<CommitGithubRepositoryFileApiRequestDTO> entity = new HttpEntity<>(requestBody, createHeaders(getAccessToken(authUser.getName())));
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("bbb");
            return null;
        }
        System.out.println("ccc");
        return responseEntity.getBody();
    }

    // Github 에 파일을 커밋하는 메서드
    // https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents
    public String updateGithubRepositoryFile(CustomOAuth2User authUser, CommitGithubRepositoryFileRequestDTO requestDTO, String sha) {
        String url = GITHUB_API_URL + "/repos/" + authUser.getUserNickname() + "/" + authUser.getUserRepo() + "/contents/" + requestDTO.getPath();

        CommitGithubRepositoryFileApiRequestDTO requestBody = CommitGithubRepositoryFileApiRequestDTO.builder()
                .message("refactor: [" + LocalDateTime.now() + "] " + requestDTO.getPath())
                .content(Base64.getEncoder().encodeToString(requestDTO.getContent().getBytes()))
                .sha(sha)
                .build();

        HttpEntity<CommitGithubRepositoryFileApiRequestDTO> entity = new HttpEntity<>(requestBody, createHeaders(getAccessToken(authUser.getName())));
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        return responseEntity.getBody();
    }

    // Github 에서 파일을 가져오는 메서드
    // https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#get-repository-content
    private GithubRepositoryFileApiResponseDTO getGithubRepositoryFile(CustomOAuth2User authUser, String path) {
        System.out.println("888");
        String url = GITHUB_API_URL + "/repos/" + authUser.getUserNickname() + "/" + authUser.getUserRepo() + "/contents/" + path;
        System.out.println("url = " + url);
        HttpEntity<GithubRepositoryFileApiResponseDTO> entity = new HttpEntity<>(createHeaders(getAccessToken(authUser.getName())));

        try {
            ResponseEntity<GithubRepositoryFileApiResponseDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
            });
            System.out.println("000");
            return responseEntity.getBody();
        } catch (Exception e) {
            System.out.println("999");
            return null;
        }

    }
}

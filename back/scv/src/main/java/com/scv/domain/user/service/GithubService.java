package com.scv.domain.user.service;

import com.scv.domain.oauth2.AuthUser;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.request.CreateGithubRepositoryRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubService {

    public String getDefaultBranch(String token) {
        String url = GITHUB_API_URL;

        HttpHeaders headers = createHeaders(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return (String) response.getBody().get("default_branch");
    }

    public String createBlob(String token, String content) {
        String url = GITHUB_API_URL + "/git/blobs";

        HttpHeaders headers = createHeaders(token);
        String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());
        String requestBody = String.format("{\"content\": \"%s\", \"encoding\": \"base64\"}", encodedContent);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return (String) response.getBody().get("sha");
    }

    public String createTree(String token, String blobSha, String path) {
        String url = GITHUB_API_URL + "/git/trees";

        HttpHeaders headers = createHeaders(token);
        String requestBody = String.format(
                "{\"tree\": [{\"path\": \"%s\", \"mode\": \"100644\", \"type\": \"blob\", \"sha\": \"%s\"}]}",
                path, blobSha
        );

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return (String) response.getBody().get("sha");
    }

    public String createCommit(String token, String treeSha, String parentSha, String message) {
        String url = GITHUB_API_URL + "/git/commits";

        HttpHeaders headers = createHeaders(token);
        String requestBody = String.format(
                "{\"message\": \"%s\", \"tree\": \"%s\", \"parents\": [\"%s\"]}",
                message, treeSha, parentSha
        );

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return (String) response.getBody().get("sha");
    }

    public void updateBranch(String token, String commitSha, String branch) {
        String url = GITHUB_API_URL + "/git/refs/heads/" + branch;

        HttpHeaders headers = createHeaders(token);
        String requestBody = String.format("{\"sha\": \"%s\"}", commitSha);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        restTemplate.exchange(url, HttpMethod.PATCH, entity, Void.class);
    }

    public void commitFile(String token, String content, String path, String message, String parentSha) {
        // 1. 기본 브랜치를 조회합니다.
        String defaultBranch = getDefaultBranch(token);

        // 2. Blob 생성
        String blobSha = createBlob(token, content);

        // 3. 트리 생성
        String treeSha = createTree(token, blobSha, path);

        // 4. 커밋 생성
        String commitSha = createCommit(token, treeSha, parentSha, message);

        // 5. 브랜치 업데이트
        updateBranch(token, commitSha, defaultBranch);
    }

    public String getGithubUserEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        List<Map<String, Object>> emails = response.getBody();

        if (emails != null) {
            for (Map<String, Object> email : emails) {
                if ((Boolean) email.get("primary")) {
                    return (String) email.get("email");
                }
            }
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

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
    private HttpHeaders createHeaders(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken(username));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github+json")));
        return headers;
    }

    // Github Repository 생성 API
    // https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#create-a-repository-for-the-authenticated-user
    public boolean createGithubRepository(CustomOAuth2User user, CreateGithubRepositoryRequestDTO requestDTO) {
        String url = GITHUB_API_URL + "/user/repos";

        ExternalCreateGithubRepositoryRequestDTO requestBody = ExternalCreateGithubRepositoryRequestDTO.builder()
                .name(requestDTO.getRepoName())
                .description(requestDTO.getRepoName() + " 저장소")
                .build();

        HttpEntity<ExternalCreateGithubRepositoryRequestDTO> entity = new HttpEntity<>(requestBody, createHeaders(user.getName()));
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    // Github Repository 목록 조회 API
    // https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
    private List<ExternalGithubRepositoryResponseDTO> getGithubRepositoryList(CustomOAuth2User user) {
        String url = GITHUB_API_URL + "/users/" + user.getUserNickname() + "/repos";

        HttpEntity<String> entity = new HttpEntity<>(createHeaders(user.getName()));
        ResponseEntity<List<ExternalGithubRepositoryResponseDTO>> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        return exchange.getBody();
    }

    // Github Repository 이름들을 Set 으로 반환
    public Set<String> getGithubRepositoryNames(CustomOAuth2User user) {
        return getGithubRepositoryList(user).stream()
                .map(ExternalGithubRepositoryResponseDTO::getName)
                .collect(Collectors.toSet());
    }

//    public String cloneGithubRepositoryFile(CustomOAuth2User user) {
//        String url = GITHUB_API_URL + "/repos/" + user.getUserNickname() + "/" +
//    }

    // https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents
//    public void commitGithubRepositoryFile(CustomOAuth2User user, ExternalCommitGithubRepositoryFileRequestDTO requestDTO) {
//        String url = GITHUB_API_URL + "/repos/" + user.getUserNickname() + "/" + user.getUserRepo() + "/contents/" + requestDTO.getPath();
//
//        HttpEntity<ExternalCommitGithubRepositoryFileRequestDTO> entity = new HttpEntity<>(requestDTO, createHeaders(user.getName()));
//        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//    }

    public void commitGithubRepositoryFile(CustomOAuth2User user, String path, String message, String content) {
        ExternalCommitGithubRepositoryFileRequestDTO requestDTO = ExternalCommitGithubRepositoryFileRequestDTO.builder()
                .path(path)
                .message(message)
                .content(Base64.getEncoder().encodeToString(content.getBytes()))
                .build();
        String url = GITHUB_API_URL + "/repos/" + user.getUserNickname() + "/" + "zzz" + "/contents/" + requestDTO.getPath();

        HttpEntity<ExternalCommitGithubRepositoryFileRequestDTO> entity = new HttpEntity<>(requestDTO, createHeaders(user.getName()));
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }
}

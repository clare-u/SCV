package com.scv.domain.user.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.user.dto.request.CreateGithubRepoApiRequestDTO;
import com.scv.domain.user.dto.request.ExportGithubRepoFileApiRequestDTO;
import com.scv.domain.user.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoFileApiResponseDTO;
import com.scv.domain.user.exception.GithubNotFoundException;
import com.scv.domain.user.util.GithubUrlBuilder;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Qualifier("githubApiRestTemplateService")
public class GithubApiRestTemplateService implements GithubApiService {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final GithubUrlBuilder githubUrlBuilder;
    private final RestTemplate restTemplate;

    // 인증된 유저의 AccessToken 반환
    private String getAccessToken(CustomOAuth2User authUser) {
        return oAuth2AuthorizedClientService.loadAuthorizedClient("github", authUser.getName())
                .getAccessToken()
                .getTokenValue();
    }

    // GitHub REST API 용 Header 반환
    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github+json")));
        return headers;
    }

    // GitHub REST API 용 Header 반환
    private HttpHeaders createHeaders(CustomOAuth2User authUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken(authUser));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.parseMediaType("application/vnd.github+json")));
        return headers;
    }

    @Override
    public List<GithubEmailApiResponseDTO> getGithubEmailList(String accessToken) {
        String url = githubUrlBuilder.buildEmailListUrl();

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders(accessToken));
        ResponseEntity<List<GithubEmailApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }

    @Override
    public void createGithubRepo(CustomOAuth2User authUser, CreateGithubRepoApiRequestDTO requestDTO) {
        String url = githubUrlBuilder.buildCreateRepoUrl();

        HttpEntity<CreateGithubRepoApiRequestDTO> entity = new HttpEntity<>(requestDTO, createHeaders(authUser));
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }

    @Override
    public List<GithubRepoApiResponseDTO> getGithubRepoList(CustomOAuth2User authUser) {
        String url = githubUrlBuilder.buildRepoListUrl(authUser);

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders(authUser));
        ResponseEntity<List<GithubRepoApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }

    @Override
    public Optional<GithubRepoFileApiResponseDTO> importGithubRepoFile(CustomOAuth2User authUser, DataSet dataName, String modelName) {
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

    @Override
    public void exportGithubRepoFile(CustomOAuth2User authUser, DataSet dataName, String modelName, ExportGithubRepoFileApiRequestDTO requestDTO) {
        String url = githubUrlBuilder.buildRepoFileUrl(authUser, dataName, modelName);

        HttpEntity<ExportGithubRepoFileApiRequestDTO> entity = new HttpEntity<>(requestDTO, createHeaders(authUser));
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

}

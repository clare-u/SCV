package com.scv.domain.user.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.user.dto.request.CreateGithubRepoApiRequestDTO;
import com.scv.domain.user.dto.request.ExportGithubRepoFileApiRequestDTO;
import com.scv.domain.user.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoFileApiResponseDTO;
import com.scv.domain.user.exception.GithubNotFoundException;
import com.scv.domain.user.exception.GithubUnauthorizedException;
import com.scv.domain.user.util.GithubUrlBuilder;
import com.scv.global.jwt.service.RedisTokenService;
import com.scv.global.jwt.util.CookieUtil;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.global.oauth2.service.RedisOAuth2AuthorizedClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.scv.global.jwt.util.JwtUtil.ACCESS_TOKEN_NAME;
import static com.scv.global.jwt.util.JwtUtil.REFRESH_TOKEN_NAME;

@Service
@RequiredArgsConstructor
public class GithubApiRestTemplateService implements GithubApiService {

    private final RedisTokenService redisTokenService;
    private final RedisOAuth2AuthorizedClientService redisOAuth2AuthorizedClientService;
    private final RestTemplate restTemplate;

    // 인증된 유저의 AccessToken 반환
    private String getAccessToken(CustomOAuth2User authUser) {
        String oauthToken = redisOAuth2AuthorizedClientService.getAccessToken("github", authUser.getName());

        if (oauthToken == null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

            CookieUtil.getCookie(request, ACCESS_TOKEN_NAME)
                    .ifPresent(cookie -> redisTokenService.addToBlacklist(cookie.getValue()));

            CookieUtil.getCookie(request, REFRESH_TOKEN_NAME)
                    .ifPresent(cookie -> redisTokenService.deleteFromWhitelist(cookie.getValue()));

            CookieUtil.deleteCookie(response, ACCESS_TOKEN_NAME);
            CookieUtil.deleteCookie(response, REFRESH_TOKEN_NAME);

            throw GithubUnauthorizedException.getInstance();
        }

        return oauthToken;
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
        String url = GithubUrlBuilder.buildEmailListUrl();

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders(accessToken));
        ResponseEntity<List<GithubEmailApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }

    @Override
    public void createGithubRepo(CustomOAuth2User authUser, CreateGithubRepoApiRequestDTO requestDTO) {
        String url = GithubUrlBuilder.buildCreateRepoUrl();

        HttpEntity<CreateGithubRepoApiRequestDTO> entity = new HttpEntity<>(requestDTO, createHeaders(authUser));
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }

    @Override
    public List<GithubRepoApiResponseDTO> getGithubRepoList(CustomOAuth2User authUser) {
        String url = GithubUrlBuilder.buildRepoListUrl(authUser);

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders(authUser));
        ResponseEntity<List<GithubRepoApiResponseDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }

    @Override
    public Optional<GithubRepoFileApiResponseDTO> importGithubRepoFile(CustomOAuth2User authUser, DataSet dataName, String modelName) {
        String url = GithubUrlBuilder.buildRepoFileUrl(authUser, dataName, modelName);

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
        String url = GithubUrlBuilder.buildRepoFileUrl(authUser, dataName, modelName);

        HttpEntity<ExportGithubRepoFileApiRequestDTO> entity = new HttpEntity<>(requestDTO, createHeaders(authUser));
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

}

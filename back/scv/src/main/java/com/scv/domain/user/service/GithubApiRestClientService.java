package com.scv.domain.user.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.user.dto.request.CreateGithubRepoApiRequestDTO;
import com.scv.domain.user.dto.request.ExportGithubRepoFileApiRequestDTO;
import com.scv.domain.user.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoFileApiResponseDTO;
import com.scv.domain.user.exception.*;
import com.scv.domain.user.util.GithubUrlBuilder;
import com.scv.global.jwt.service.RedisTokenService;
import com.scv.global.jwt.util.CookieUtil;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.global.oauth2.service.RedisOAuth2AuthorizedClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

import static com.scv.global.jwt.util.JwtUtil.ACCESS_TOKEN_NAME;
import static com.scv.global.jwt.util.JwtUtil.REFRESH_TOKEN_NAME;

@Service
@Primary
@RequiredArgsConstructor
public class GithubApiRestClientService implements GithubApiService {

    private final RedisTokenService redisTokenService;
    private final RedisOAuth2AuthorizedClientService redisOAuth2AuthorizedClientService;

    private static final RestClient restClient = RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
            .defaultStatusHandler(HttpStatusCode::is4xxClientError, ((request, response) -> {
                switch (response.getStatusCode().value()) {
                    case 401 -> throw GithubUnauthorizedException.getInstance();
                    case 403 -> throw GithubForbiddenException.getInstance();
                    case 404 -> throw GithubNotFoundException.getInstance();
                    case 409 -> throw GithubConflictException.getInstance();
                    case 422 -> throw GithubUnprocessableEntityException.getInstance();
                    default -> throw GithubBadRequestException.getInstance();
                }
            }))
            .build();

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

    @Override
    public List<GithubEmailApiResponseDTO> getGithubEmailList(String accessToken) {
        return restClient.get()
                .uri(GithubUrlBuilder.buildEmailListUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public void createGithubRepo(CustomOAuth2User authUser, CreateGithubRepoApiRequestDTO requestDTO) {
        restClient.post()
                .uri(GithubUrlBuilder.buildCreateRepoUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken(authUser))
                .body(requestDTO)
                .retrieve()
                .toEntity(Void.class);
    }

    @Override
    public List<GithubRepoApiResponseDTO> getGithubRepoList(CustomOAuth2User authUser) {
        return restClient.get()
                .uri(GithubUrlBuilder.buildRepoListUrl(authUser))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken(authUser))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public Optional<GithubRepoFileApiResponseDTO> importGithubRepoFile(CustomOAuth2User authUser, DataSet dataName, String modelName) {
        try {
            return restClient.get()
                    .uri(GithubUrlBuilder.buildRepoFileUrl(authUser, dataName, modelName))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken(authUser))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (GithubNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void exportGithubRepoFile(CustomOAuth2User authUser, DataSet dataName, String modelName, ExportGithubRepoFileApiRequestDTO requestDTO) {
        restClient.put()
                .uri(GithubUrlBuilder.buildRepoFileUrl(authUser, dataName, modelName))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken(authUser))
                .body(requestDTO)
                .retrieve()
                .toEntity(Void.class);
    }

}

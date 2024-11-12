package com.scv.domain.user.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.user.dto.request.CreateGithubRepoApiRequestDTO;
import com.scv.domain.user.dto.request.ExportGithubRepoBlockFileApiRequestDTO;
import com.scv.domain.user.dto.request.ExportGithubRepoBlockFileRequestDTO;
import com.scv.domain.user.dto.request.LinkGithubRepoRequestDTO;
import com.scv.domain.user.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoBlockFileApiResponseDTO;
import com.scv.domain.user.exception.UserNotFoundException;
import com.scv.global.jwt.service.RedisTokenService;
import com.scv.global.jwt.util.JwtUtil;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.domain.user.dto.response.GithubRepoBlockFileResponseDTO;
import com.scv.domain.user.exception.GithubConflictException;
import com.scv.domain.user.exception.GithubNotFoundException;
import com.scv.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class GithubService {

    private final GithubApiService githubApiService;
    private final RedisTokenService redisTokenService;
    private final UserRepository userRepository;

    public GithubService(@Qualifier("githubApiRestClientService") GithubApiService githubApiService, RedisTokenService redisTokenService, UserRepository userRepository) {
        this.githubApiService = githubApiService;
        this.redisTokenService = redisTokenService;
        this.userRepository = userRepository;
    }

    // 깃허브에서 primary email 을 조회하는 메서드
    public String getGithubPrimaryEmail(String accessToken) {
        return githubApiService.getGithubEmailList(accessToken).stream()
                .filter(GithubEmailApiResponseDTO::getPrimary)
                .map(GithubEmailApiResponseDTO::getEmail)
                .findFirst()
                .orElse(null);
    }

    // 깃허브 새 리포를 메인 리포로 설정 서비스 로직
    public String linkNewGithubRepo(CustomOAuth2User authUser, LinkGithubRepoRequestDTO requestDTO, String accessToken) {
        if (getGithubRepoNames(authUser).contains(requestDTO.getRepoName())) {
            throw GithubConflictException.getInstance();
        }

        CreateGithubRepoApiRequestDTO newRequestDTO = CreateGithubRepoApiRequestDTO.builder()
                .name(requestDTO.getRepoName())
                .description(getNewGithubRepoDescription(requestDTO.getRepoName()))
                .build();
        githubApiService.createGithubRepo(authUser, newRequestDTO);

        userRepository.updateUserRepoById(authUser.getUserId(), requestDTO.getRepoName());
        redisTokenService.addToBlacklist(accessToken);
        return JwtUtil.createAccessToken(userRepository.findById(authUser.getUserId()).orElseThrow(UserNotFoundException::getInstance));
    }

    // 깃허브 기존 리포를 메인 리포로 설정 서비스 로직
    public String linkCurrentGithubRepo(CustomOAuth2User authUser, LinkGithubRepoRequestDTO requestDTO, String accessToken) {
        if (!getGithubRepoNames(authUser).contains(requestDTO.getRepoName())) {
            throw GithubNotFoundException.getInstance();
        }

        userRepository.updateUserRepoById(authUser.getUserId(), requestDTO.getRepoName());
        redisTokenService.addToBlacklist(accessToken);
        return JwtUtil.createAccessToken(userRepository.findById(authUser.getUserId()).orElseThrow(UserNotFoundException::getInstance));
    }

    // 깃허브에서 모델 import 서비스 로직
    public GithubRepoBlockFileResponseDTO importGithubRepoBlockFile(CustomOAuth2User auth2User, DataSet dataName, String modelName) {
        return GithubRepoBlockFileResponseDTO.builder()
                .content(githubApiService.importGithubRepoBlockFile(auth2User, dataName, modelName)
                        .map(GithubRepoBlockFileApiResponseDTO::getContent)
                        .map(encodedContent -> new String(Base64.getDecoder().decode(encodedContent.replaceAll("\\s", ""))))
                        .orElse(null))
                .build();
    }

    // 깃허브에 모델 export 서비스 로직
    public void exportGithubRepoBlockFile(CustomOAuth2User auth2User, ExportGithubRepoBlockFileRequestDTO requestDTO) {
        String sha = githubApiService.importGithubRepoBlockFile(auth2User, requestDTO.getDataName(), requestDTO.getModelName())
                .map(GithubRepoBlockFileApiResponseDTO::getSha)
                .orElse(null);

        ExportGithubRepoBlockFileApiRequestDTO newRequestDTO = ExportGithubRepoBlockFileApiRequestDTO.builder()
                .content(Base64.getEncoder().encodeToString(requestDTO.getContent().getBytes(StandardCharsets.UTF_8)))
                .message(getExportMessage(requestDTO.getMessage(), requestDTO.getModelName(), requestDTO.getVersionNo(), sha))
                .sha(sha)
                .build();
        githubApiService.exportGithubRepoBlockFile(auth2User, requestDTO.getDataName(), requestDTO.getModelName(), newRequestDTO);
    }

    // 깃허브 새 리포 설명 메시지 반환
    private String getNewGithubRepoDescription(String repoName) {
        return "Welcome " + repoName + "!";
    }

    // 깃허브에서 Repository 리스트의 이름들을 Set 으로 반환
    private Set<String> getGithubRepoNames(CustomOAuth2User authUser) {
        return githubApiService.getGithubRepoList(authUser).stream()
                .map(GithubRepoApiResponseDTO::getName)
                .collect(Collectors.toSet());
    }

    // 깃허브에 모델 export 시 커밋 메시지 생성
    private String getExportMessage(String message, String modelName, Long versionNo, String sha) {
        if (message != null && !message.trim().isEmpty()) return message;
        if (sha == null) return "feat: [" + modelName + " v-" + versionNo + "] - SCV";
        return "refactor: [" + modelName + " v-" + versionNo + "] - SCV";
    }

}

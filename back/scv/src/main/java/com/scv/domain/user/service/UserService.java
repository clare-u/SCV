package com.scv.domain.user.service;

import com.scv.domain.user.domain.User;
import com.scv.domain.user.dto.request.CreateGithubRepositoryRequestDTO;
import com.scv.domain.user.dto.response.CommonSuccessResponseDTO;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.user.exception.UserNotFoundException;
import com.scv.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    private final UserRepository userRepository;

    public UserProfileResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::getInstance);
        return UserProfileResponseDTO.builder()
                .userEmail(user.getUserEmail())
                .userImageUrl(user.getUserImageUrl())
                .userNickname(user.getUserNickname())
                .build();
    }

    public CommonSuccessResponseDTO createGithubRepository(String userName, CreateGithubRepositoryRequestDTO requestDTO) {
        String accessToken = getGithubAccessToken(userName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"name\":\"%s\", \"description\":\"%s 저장소\"}", requestDTO.getName(), requestDTO.getName());

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.POST,
                entity,
                String.class
        );

        return CommonSuccessResponseDTO.builder()
                .message("Success")
                .build();
    }

    private String getGithubAccessToken(String userName) {
        return oAuth2AuthorizedClientService.loadAuthorizedClient("github", userName)
                .getAccessToken()
                .getTokenValue();
    }
}

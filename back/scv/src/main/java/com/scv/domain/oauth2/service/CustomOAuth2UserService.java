package com.scv.domain.oauth2.service;

import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.oauth2.OAuth2Provider;
import com.scv.domain.oauth2.dto.OAuth2UserDTO;
import com.scv.domain.oauth2.user.OAuth2GithubResponse;
import com.scv.domain.oauth2.user.OAuth2Response;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2Provider oAuth2Provider = OAuth2Provider.from(userRequest.getClientRegistration().getRegistrationId());

        OAuth2Response oAuth2Response;

        switch (oAuth2Provider) {
            case GITHUB -> {
                String userEmail = getGithubUserEmail(userRequest.getAccessToken().getTokenValue());
                oAuth2Response = new OAuth2GithubResponse(oAuth2User.getAttributes(), userEmail);
            }
            default -> {
                return null;
            }
        }

        User existUser = userRepository.findByUserEmail(oAuth2Response.getUserEmail()).orElse(null);

        if (existUser == null) {
            User user = User.builder()
                    .userUuid(UUID.randomUUID().toString())
                    .userEmail(oAuth2Response.getUserEmail())
                    .userImageUrl(oAuth2Response.getUserImageUrl())
                    .userNickname(oAuth2Response.getUserNickname())
                    .userCreatedAt(oAuth2Response.getUserCreatedAt().toLocalDateTime())
                    .userUpdatedAt(oAuth2Response.getUserUpdatedAt().toLocalDateTime())
                    .build();
            User savedUser = userRepository.save(user);

            OAuth2UserDTO oAuth2UserDTO = OAuth2UserDTO.builder()
                    .userId(savedUser.getUserId())
                    .userUuid(savedUser.getUserUuid())
                    .userEmail(savedUser.getUserEmail())
                    .userImageUrl(savedUser.getUserImageUrl())
                    .userNickname(savedUser.getUserNickname())
                    .userCreatedAt(savedUser.getUserCreatedAt())
                    .userUpdatedAt(savedUser.getUserUpdatedAt())
                    .userIsDeleted(savedUser.isUserIsDeleted())
                    .build();
            return new CustomOAuth2User(oAuth2UserDTO);
        } else {
            OAuth2UserDTO oAuth2UserDTO = OAuth2UserDTO.builder()
                    .userId(existUser.getUserId())
                    .userUuid(existUser.getUserUuid())
                    .userEmail(existUser.getUserEmail())
                    .userImageUrl(existUser.getUserImageUrl())
                    .userNickname(existUser.getUserNickname())
                    .userCreatedAt(existUser.getUserCreatedAt())
                    .userUpdatedAt(existUser.getUserUpdatedAt())
                    .userIsDeleted(existUser.isUserIsDeleted())
                    .build();
            return new CustomOAuth2User(oAuth2UserDTO);
        }
    }

    private String getGithubUserEmail(String accessToken) {
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
}

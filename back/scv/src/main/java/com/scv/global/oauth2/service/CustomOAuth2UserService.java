package com.scv.global.oauth2.service;

import com.scv.domain.user.service.GithubService;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.global.oauth2.enums.OAuth2Provider;
import com.scv.global.oauth2.dto.OAuth2UserDTO;
import com.scv.global.oauth2.user.OAuth2GithubResponse;
import com.scv.global.oauth2.user.OAuth2Response;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final GithubService githubService;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2Provider oAuth2Provider = OAuth2Provider.from(userRequest.getClientRegistration().getRegistrationId());

        OAuth2Response oAuth2Response;

        switch (oAuth2Provider) {
            case GITHUB -> {
                String userEmail = githubService.getGithubPrimaryEmail(userRequest.getAccessToken().getTokenValue());
                oAuth2Response = new OAuth2GithubResponse(oAuth2User.getAttributes(), userEmail);
            }
            default -> {
                return null;
            }
        }

        User existUser = userRepository.findByUserEmail(oAuth2Response.getUserEmail()).orElse(null);

        // 신규 회원
        if (existUser == null) {
            return null;
//            User user = User.builder()
//                    .userUuid(UUID.randomUUID().toString())
//                    .userEmail(oAuth2Response.getUserEmail())
//                    .userImageUrl(oAuth2Response.getUserImageUrl())
//                    .userNickname(oAuth2Response.getUserNickname())
//                    .userCreatedAt(oAuth2Response.getUserCreatedAt())
//                    .userUpdatedAt(oAuth2Response.getUserUpdatedAt())
//                    .build();
//            User savedUser = userRepository.save(user);
//
//            OAuth2UserDTO oAuth2UserDTO = OAuth2UserDTO.builder()
//                    .userId(savedUser.getUserId())
//                    .userUuid(savedUser.getUserUuid())
//                    .userNickname(savedUser.getUserNickname())
//                    .userRepo(savedUser.getUserRepo())
//                    .build();
//            return new CustomOAuth2User(oAuth2UserDTO);
        }
        // 기존 회원
        else {
            OAuth2UserDTO oAuth2UserDTO = OAuth2UserDTO.builder()
                    .userId(existUser.getUserId())
                    .userUuid(existUser.getUserUuid())
                    .userNickname(existUser.getUserNickname())
                    .userRepo(existUser.getUserRepo())
                    .build();
            return new CustomOAuth2User(oAuth2UserDTO);
        }
    }

}

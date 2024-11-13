package com.scv.domain.user.service;

import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.global.oauth2.auth.CustomOAuth2User;

public interface UserService {

    // 유저 프로필 조회 서비스 로직
    UserProfileResponseDTO getUserProfile(CustomOAuth2User authUser);


    // 깃허브 리포 연동 해제 서비스 로직
    String unLinkGithubRepo(CustomOAuth2User authUser, String accessToken);

}

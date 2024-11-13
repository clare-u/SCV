package com.scv.domain.user.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.user.dto.request.ExportGithubRepoFileRequestDTO;
import com.scv.domain.user.dto.request.LinkGithubRepoRequestDTO;
import com.scv.domain.user.dto.response.GithubRepoFileResponseDTO;
import com.scv.global.oauth2.auth.CustomOAuth2User;

public interface GithubService {

    // 깃허브에서 primary email 을 조회하는 메서드
    String getGithubPrimaryEmail(String accessToken);

    // 깃허브 새 리포를 메인 리포로 설정 서비스 로직
    String linkNewGithubRepo(CustomOAuth2User authUser, LinkGithubRepoRequestDTO requestDTO, String accessToken);

    // 깃허브 기존 리포를 메인 리포로 설정 서비스 로직
    String linkCurrentGithubRepo(CustomOAuth2User authUser, LinkGithubRepoRequestDTO requestDTO, String accessToken);

    // 깃허브에서 모델 import 서비스 로직
    GithubRepoFileResponseDTO importGithubRepoFile(CustomOAuth2User auth2User, DataSet dataName, String modelName);

    // 깃허브에 모델 export 서비스 로직
    void exportGithubRepoFile(CustomOAuth2User auth2User, ExportGithubRepoFileRequestDTO requestDTO);

}

package com.scv.domain.user.service;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.user.dto.request.CreateGithubRepoApiRequestDTO;
import com.scv.domain.user.dto.request.ExportGithubRepoFileApiRequestDTO;
import com.scv.domain.user.dto.response.GithubEmailApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoApiResponseDTO;
import com.scv.domain.user.dto.response.GithubRepoFileApiResponseDTO;
import com.scv.global.oauth2.auth.CustomOAuth2User;

import java.util.List;
import java.util.Optional;

public interface GithubApiService {

    /**
     * <p>GitHub 에서 email 목록을 조회하는 메서드</p>
     * <p>API Endpoint: <a href="https://api.github.com/user/emails">[GET] https://api.github.com/user/emails</a></p>
     * <p>GitHub API Document: <a href="https://docs.github.com/ko/rest/users/emails?apiVersion=2022-11-28#list-email-addresses-for-the-authenticated-user">List email addresses for the authenticated user</a></p>
     */
    List<GithubEmailApiResponseDTO> getGithubEmailList(String accessToken);

    /**
     * <p>GitHub 에 Repository 를 생성하는 메서드</p>
     * <p>API Endpoint: <a href="https://api.github.com/user/repos">[POST] https://api.github.com/user/repos</a></p>
     * <p>GitHub API Document: <a href="https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#create-a-repository-for-the-authenticated-user">Create a repository for the authenticated user</a></p>
     */
    void createGithubRepo(CustomOAuth2User authUser, CreateGithubRepoApiRequestDTO requestDTO);

    /**
     * <p>GitHub 에 README 파일을 export 하는 메서드</p>
     * <p>API Endpoint: <a href="https://api.github.com/repos/{userNickname}/{userRepo}/contents/README.md">[PUT] https://api.github.com/repos/{userNickname}/{userRepo}/contents/README.md</a></p>
     * <p>GitHub API Document: <a href="https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents">Create or update file contents</a></p>
     */
    void createGithubRepoReadme(CustomOAuth2User authUser, String repoName, ExportGithubRepoFileApiRequestDTO requestDTO);

    /**
     * <p>GitHub 에서 Repository 목록을 조회하는 메서드</p>
     * <p>API Endpoint: <a href="https://api.github.com/users/{userNickname}/repos">[GET] https://api.github.com/users/{userNickname}/repos</a></p>
     * <p>GitHub API Document: <a href="https://docs.github.com/ko/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user">List repositories for a user</a></p>
     */
    List<GithubRepoApiResponseDTO> getGithubRepoList(CustomOAuth2User authUser);

    /**
     * <p>GitHub 에서 변환된 파이썬 파일을 import 하는 메서드</p>
     * <p>API Endpoint: <a href="https://api.github.com/repos/{userNickname}/{userRepo}/contents/{dataName}/{modelName}/block.json">[GET] https://api.github.com/repos/{userNickname}/{userRepo}/contents/{dataName}/{modelName}/block.json</a></p>
     * <p>GitHub API Document: <a href="https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#get-repository-content">Get repository content</a></p>
     */
    Optional<GithubRepoFileApiResponseDTO> importGithubRepoFile(CustomOAuth2User authUser, DataSet dataName, String modelName);

    /**
     * <p>GitHub 에 변환된 파이썬 파일을 export 하는 메서드</p>
     * <p>API Endpoint: <a href="https://api.github.com/repos/{userNickname}/{userRepo}/contents/{dataName}/{modelName}/block.json">[PUT] https://api.github.com/repos/{userNickname}/{userRepo}/contents/{dataName}/{modelName}/block.json</a></p>
     * <p>GitHub API Document: <a href="https://docs.github.com/ko/rest/repos/contents?apiVersion=2022-11-28#create-or-update-file-contents">Create or update file contents</a></p>
     */
    void exportGithubRepoFile(CustomOAuth2User authUser, DataSet dataName, String modelName, ExportGithubRepoFileApiRequestDTO requestDTO);

}

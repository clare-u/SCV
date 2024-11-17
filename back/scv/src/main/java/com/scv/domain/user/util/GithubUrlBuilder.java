package com.scv.domain.user.util;

import com.scv.domain.data.enums.DataSet;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import org.springframework.web.util.UriComponentsBuilder;

public class GithubUrlBuilder {
    private static final String GITHUB_API_URL = "https://api.github.com";

    private static final String GET_EMAIL_LIST_PATH = "/user/emails";

    private static final String GET_REPO_LIST_PATH = "/users/{userNickname}/repos";
    private static final String CREATE_REPO_PATH = "/user/repos";

    private static final String CREATE_REPO_README_PATH = "/repos/{userNickname}/{userRepo}/contents/README.md";

    private static final String REPO_FILE_PATH = "/repos/{userNickname}/{userRepo}/contents/{dataName}/{modelName}/model.py";

    private GithubUrlBuilder() {
    }

    public static String buildEmailListUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(GITHUB_API_URL)
                .path(GET_EMAIL_LIST_PATH)
                .toUriString();
    }

    public static String buildRepoListUrl(CustomOAuth2User authUser) {
        return UriComponentsBuilder
                .fromHttpUrl(GITHUB_API_URL)
                .path(GET_REPO_LIST_PATH)
                .queryParam("per_page", 100)
                .buildAndExpand(authUser.getUserNickname())
                .toUriString();
    }

    public static String buildCreateRepoUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(GITHUB_API_URL)
                .path(CREATE_REPO_PATH)
                .toUriString();
    }

    public static String buildCreateRepoReadmeUrl(CustomOAuth2User authUser, String repoName) {
        return UriComponentsBuilder
                .fromHttpUrl(GITHUB_API_URL)
                .path(CREATE_REPO_README_PATH)
                .buildAndExpand(authUser.getUserNickname(), repoName)
                .toUriString();
    }

    public static String buildRepoFileUrl(CustomOAuth2User authUser, DataSet dataName, String modelName) {
        return UriComponentsBuilder
                .fromHttpUrl(GITHUB_API_URL)
                .path(REPO_FILE_PATH)
                .buildAndExpand(authUser.getUserNickname(), authUser.getUserRepo(), dataName, modelName)
                .toUriString();
    }

}

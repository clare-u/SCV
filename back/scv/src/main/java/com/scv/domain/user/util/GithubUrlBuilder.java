package com.scv.domain.user.util;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.oauth2.CustomOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GithubUrlBuilder {
    private static final String GITHUB_API_URL = "https://api.github.com";

    private static final String GET_EMAIL_LIST_PATH = "/user/emails";

    private static final String GET_REPO_LIST_PATH = "/users/{userNickname}/repos";
    private static final String CREATE_REPO_PATH = "/user/repos";

    private static final String REPO_FILE_PATH = "/repos/{userNickname}/{userRepo}/contents/{dataName}/{modelName}/block.json";

    public String buildEmailListUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(GITHUB_API_URL)
                .path(GET_EMAIL_LIST_PATH)
                .toUriString();
    }

    public String buildRepoListUrl(CustomOAuth2User authUser) {
        return UriComponentsBuilder
                .fromHttpUrl(GITHUB_API_URL)
                .path(GET_REPO_LIST_PATH)
                .buildAndExpand(authUser.getUserNickname())
                .toUriString();
    }

    public String buildCreateRepoUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(GITHUB_API_URL)
                .path(CREATE_REPO_PATH)
                .toUriString();
    }

    public String buildRepoFileUrl(CustomOAuth2User authUser, DataSet dataName, String modelName) {
        return UriComponentsBuilder
                .fromHttpUrl(GITHUB_API_URL)
                .path(REPO_FILE_PATH)
                .buildAndExpand(authUser.getUserNickname(), authUser.getUserRepo(), dataName, modelName)
                .toUriString();
    }
}

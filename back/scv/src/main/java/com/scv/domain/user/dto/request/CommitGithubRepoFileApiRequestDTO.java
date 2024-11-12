package com.scv.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommitGithubRepoFileApiRequestDTO {

    @JsonProperty("message")
    private String message;

    @JsonProperty("content")
    private String content;

    @JsonProperty("sha")
    private String sha;
}

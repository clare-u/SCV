package com.scv.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CommitGithubRepositoryFileRequestDTO {

    @JsonProperty("path")
    private String path;

    @JsonProperty("message")
    private String message;

    @JsonProperty("content")
    private String content;
}

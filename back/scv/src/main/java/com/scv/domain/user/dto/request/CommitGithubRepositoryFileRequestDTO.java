package com.scv.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CommitGithubRepositoryFileRequestDTO {

    @JsonProperty("path")
    private String path;

    @JsonProperty("content")
    private String content;
}

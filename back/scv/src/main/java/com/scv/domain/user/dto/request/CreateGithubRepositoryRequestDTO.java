package com.scv.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CreateGithubRepositoryRequestDTO {

    @JsonProperty("repoName")
    private String repoName;
}

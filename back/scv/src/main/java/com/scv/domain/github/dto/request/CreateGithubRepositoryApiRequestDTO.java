package com.scv.domain.github.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateGithubRepositoryApiRequestDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;
}

package com.scv.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GithubRepoApiResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;
}

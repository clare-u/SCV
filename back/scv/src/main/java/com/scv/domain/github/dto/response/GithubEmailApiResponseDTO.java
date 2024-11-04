package com.scv.domain.github.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GithubEmailApiResponseDTO {

    @JsonProperty("email")
    private String email;

    @JsonProperty("verified")
    private Boolean verified;

    @JsonProperty("primary")
    private Boolean primary;

    @JsonProperty("visibility")
    private String visibility;
}

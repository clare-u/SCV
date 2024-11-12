package com.scv.domain.user.dto.response;

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

    @JsonProperty("primary")
    private Boolean primary;
}

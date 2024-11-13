package com.scv.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scv.domain.data.enums.DataSet;
import lombok.Getter;

@Getter
public class ExportGithubRepoFileRequestDTO {

    @JsonProperty("dataName")
    private DataSet dataName;

    @JsonProperty("modelName")
    private String modelName;

    @JsonProperty("versionNo")
    private Long versionNo;

    @JsonProperty("content")
    private String content;

    @JsonProperty("message")
    private String message;
}

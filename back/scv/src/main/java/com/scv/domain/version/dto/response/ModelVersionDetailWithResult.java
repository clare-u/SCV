package com.scv.domain.version.dto.response;

import com.scv.domain.result.dto.response.ResultResponseWithImages;
import com.scv.domain.version.domain.ModelVersion;
import lombok.Getter;

@Getter
public class ModelVersionDetailWithResult extends ModelVersionDetail {
    private final ResultResponseWithImages resultResponseWithImages;

    public ModelVersionDetailWithResult(ModelVersion modelVersion, ResultResponseWithImages resultResponseWithImages) {
        super(modelVersion);
        this.resultResponseWithImages = resultResponseWithImages;
    }

}

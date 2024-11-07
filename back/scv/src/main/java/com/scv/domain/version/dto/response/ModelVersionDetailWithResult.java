package com.scv.domain.version.dto.response;

import com.scv.domain.result.dto.response.ResultAnalysisResponse;
import com.scv.domain.version.domain.ModelVersion;
import lombok.Getter;

@Getter
public class ModelVersionDetailWithResult extends ModelVersionDetail {
    private final ResultAnalysisResponse resultAnalysisResponse;

    public ModelVersionDetailWithResult(ModelVersion modelVersion, ResultAnalysisResponse resultAnalysisResponse) {
        super(modelVersion);
        this.resultAnalysisResponse = resultAnalysisResponse;
    }

}

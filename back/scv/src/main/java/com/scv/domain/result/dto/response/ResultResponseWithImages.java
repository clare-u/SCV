package com.scv.domain.result.dto.response;

import com.scv.domain.result.domain.Result;
import lombok.Getter;

@Getter
public class ResultResponseWithImages extends ResultResponse {

    private final String confusionMatrix;
    private final String exampleImg;
    private final String featureActivation;
    private final String activationMaximization;

    public ResultResponseWithImages(Result result) {
        super(result);
        this.confusionMatrix = result.getConfusionMatrix();
        this.exampleImg = result.getExampleImg();
        this.featureActivation = result.getFeatureActivation();
        this.activationMaximization = result.getActivationMaximization();
    }

}

package com.scv.domain.result.dto.response;

import com.scv.global.util.ParsingUtil;
import com.scv.domain.result.domain.Result;
import com.scv.domain.result.dto.TrainInfoDTO;

public record ResultAnalysisResponse(
        String codeView,
        double testAccuracy,
        double testLoss,
        TrainInfoDTO trainInfos,
        int totalParams,
        String params,
        String confusionMatrix,
        String exampleImg,
        String featureActivation,
        String activationMaximization
) {
    public ResultAnalysisResponse(Result result) {
        this(
                result.getCode(),
                result.getTestAccuracy(),
                result.getTestLoss(),
                parseTrainInfo(result.getTrainInfo()),
                result.getTotalParams(),
                result.getParams(),
                result.getConfusionMatrix(),
                result.getExampleImg(),
                result.getFeatureActivation(),
                result.getActivationMaximization()
        );
    }

    private static TrainInfoDTO parseTrainInfo(String trainInfoJson) {
        return ParsingUtil.parseJson(trainInfoJson, TrainInfoDTO.class);
    }
}

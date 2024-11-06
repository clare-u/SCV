package com.scv.domain.result.dto.response;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.result.dto.TrainInfoDTO;

public record ResultAnalyzeResponse(
        //TODO 전체 받아서 저장
        String blockView,
        String codeView,
        DataSet dataName,
        double testAccuracy,
        double TestLoss,
        TrainInfoDTO trainInfos,
        int totalParams,
        String layerParams,
        String ConfusionMatrix,
        String exampleImg,
        String featureActivation,
        String activationMaximization
) {
}

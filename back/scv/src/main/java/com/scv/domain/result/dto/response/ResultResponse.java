package com.scv.domain.result.dto.response;

import com.scv.domain.result.domain.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultResponse {
    private final Long modelId;
    private final Long modelVersionId;
    private final String codeView;
    private final double testAccuracy;
    private final double testLoss;
    private final int totalParams;
    private final String trainInfos; // train_result_per_epoch + training_history 담기
    private final String layerParams;

    public ResultResponse(Result result) {
        this.modelId = result.getModelVersion().getModel().getId();
        this.modelVersionId = result.getId();
        this.codeView = result.getCode();
        this.testAccuracy = result.getTestAccuracy();
        this.testLoss = result.getTestLoss();
        this.trainInfos = result.getTrainInfo();
        this.totalParams = result.getTotalParams();
        this.layerParams = result.getLayerParams();
    }

}

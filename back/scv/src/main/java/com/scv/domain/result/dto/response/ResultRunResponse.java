package com.scv.domain.result.dto.response;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.result.dto.TrainInfoDTO;

public record ResultRunResponse(
        String blockView,
        String codeView,
        DataSet dataName,
        double testAccuracy,
        double testLoss,
        TrainInfoDTO trainInfos,
        int totalParams,
        String layerParams
) {
}

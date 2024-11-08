package com.scv.domain.result.dto.request;

import com.scv.domain.data.domain.Data;
import com.scv.domain.version.dto.layer.LayerDTO;

import java.util.List;

public record ResultRequest(
        ModelLayerConfig modelLayerAt,
        String dataName,
        int dataTrainCnt,
        int dataTestCnt,
        int dataLabelCnt,
        int dataEpochCnt
) {
    public ResultRequest(List<LayerDTO> layers, Data data) {
        this(new ModelLayerConfig(layers), String.valueOf(data.getName()), data.getTrainCnt(), data.getTestCnt(), data.getLabelCnt(), data.getEpochCnt());
    }

    public record ModelLayerConfig(List<LayerDTO> layers) {
    }
}

package com.scv.domain.version.dto.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.domain.version.dto.layer.LayerDTO;
import com.scv.global.util.ParsingUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ModelVersionDetail {
    private final Long modelVersionId;
    private final List<LayerDTO> layers;

    public ModelVersionDetail(ModelVersion modelVersion) {
        this.modelVersionId = modelVersion.getId();
        this.layers = parseLayers(modelVersion.getLayers());
    }

    private static List<LayerDTO> parseLayers(String layersJson) {
        return ParsingUtil.parseJson(layersJson, new TypeReference<List<LayerDTO>>() {});
    }
}

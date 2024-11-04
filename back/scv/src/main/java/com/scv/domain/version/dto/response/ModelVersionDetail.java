package com.scv.domain.version.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.domain.version.dto.layer.LayerDTO;

import java.util.List;

public record ModelVersionDetail(
        Long modelVersionId,
        List<LayerDTO> layers
) {
    public ModelVersionDetail(ModelVersion modelVersion) {
        this(
                modelVersion.getId(),
                parseLayers(modelVersion.getLayers())
        );
    }

    private static List<LayerDTO> parseLayers(String layersJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(layersJson, new TypeReference<List<LayerDTO>>() {});
        } catch (JsonMappingException e) {
            System.err.println("Error mapping JSON to LayerDTO: " + e.getMessage());
            throw new RuntimeException("Failed to map layers JSON", e);
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            throw new RuntimeException("Failed to process layers JSON", e);
        }
    }
}

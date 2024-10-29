package com.scv.domain.version.dto.layer.padding;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ReplicationPad2dDTO(
        String type,
        int padding
) implements LayerDTO {
    @Override
    public String getType() {
        return type;
    }
}
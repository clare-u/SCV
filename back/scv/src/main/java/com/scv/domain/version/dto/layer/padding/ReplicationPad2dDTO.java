package com.scv.domain.version.dto.layer.padding;

import com.scv.domain.version.dto.layer.LayerDTO;

public record ReplicationPad2dDTO(
        String name,
        int padding
) implements LayerDTO {
    public ReplicationPad2dDTO(int padding) {
        this("ReplicationPad2d", padding);
    }
}
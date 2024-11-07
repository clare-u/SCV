package com.scv.domain.result.dto.request;

import com.scv.domain.data.dto.DataDTO;

public record ResultRequest(
        String layers,
        DataDTO data
) {
}

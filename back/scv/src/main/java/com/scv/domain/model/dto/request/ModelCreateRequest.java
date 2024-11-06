package com.scv.domain.model.dto.request;

import com.scv.domain.data.enums.DataSet;

public record ModelCreateRequest(
        DataSet dataName,
        String modelName
) {
}

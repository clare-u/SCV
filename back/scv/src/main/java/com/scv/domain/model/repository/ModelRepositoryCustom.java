package com.scv.domain.model.repository;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.model.dto.response.ModelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ModelRepositoryCustom {
    Page<ModelResponse> searchMyModels(String modelName, DataSet dataName, Long userId, Pageable pageable);
    Page<ModelResponse> searchModels(String modelName, DataSet dataName, Pageable pageable);
}
package com.scv.domain.version.repository;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.version.domain.ModelVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelVersionRepositoryCustom {
    List<ModelVersion> findAllByModelIdAndDeletedFalse(Long id);

    Page<ModelVersion> findAllByUserAndIsWorkingTrueAndDeletedFalse(
            String modelName, DataSet dataName, Long userId, Pageable pageable);

    void softDeleteAllByModelId(Long modelId);

    void softDeleteById(Long modelVersionId);
}

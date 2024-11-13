package com.scv.domain.version.repository;

import com.scv.domain.version.domain.ModelVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ModelVersionRepository extends JpaRepository<ModelVersion, Long> {

    List<ModelVersion> findAllByModel_IdAndDeletedFalse(Long id);


    @Query("SELECT mv FROM ModelVersion mv JOIN mv.model m WHERE m.user.userId = :userId AND mv.isWorkingOn = true AND mv.deleted = false")
    Page<ModelVersion> findAllByUserAndIsWorkingTrueAndDeletedFalse(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE ModelVersion mv SET mv.deleted = true WHERE mv.model.id = :modelId AND mv.deleted = false")
    void softDeleteAllByModelId(@Param("modelId") Long modelId);


}

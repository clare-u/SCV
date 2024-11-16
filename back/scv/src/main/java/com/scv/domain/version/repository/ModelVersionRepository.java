package com.scv.domain.version.repository;

import com.scv.domain.version.domain.ModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ModelVersionRepository extends JpaRepository<ModelVersion, Long>, ModelVersionRepositoryCustom {

    /**
     * JPQL -> QueryDSL로 변경
     */
//    @EntityGraph(attributePaths = {"result"})
//    List<ModelVersion> findAllByModel_IdAndDeletedFalse(Long id);
//
//    @EntityGraph(attributePaths = {"model", "model.data", "result"})
//    @Query("SELECT mv FROM ModelVersion mv " +
//            "JOIN mv.model m " +
//            "WHERE mv.model.user.userId = :userId " +
//            "AND (:modelName IS NULL OR m.name LIKE CONCAT('%', :modelName, '%')) " +
//            "AND (:dataName IS NULL OR m.data.name = :dataName) " +
//            "AND mv.isWorkingOn = true " +
//            "AND mv.deleted = false")
//    Page<ModelVersion> findAllByUserAndIsWorkingTrueAndDeletedFalse(
//            @Param("modelName") String modelName,
//            @Param("dataName") DataSet dataName,
//            @Param("userId") Long userId,
//            Pageable pageable
//    );
//
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE ModelVersion mv SET mv.deleted = true WHERE mv.model.id = :modelId AND mv.deleted = false")
//    void softDeleteAllByModelId(@Param("modelId") Long modelId);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE ModelVersion mv SET mv.deleted = true WHERE mv.id = :modelVersionId")
//    void softDeleteById(@Param("modelVersionId") Long modelVersionId);

}

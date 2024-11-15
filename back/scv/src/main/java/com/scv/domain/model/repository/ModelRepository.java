package com.scv.domain.model.repository;

import com.scv.domain.model.domain.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long>, ModelRepositoryCustom {
    /**
     * JPQL -> QueryDSL로 변경
     */
//    @EntityGraph(attributePaths = {"data", "user"})
//    @Query("SELECT m FROM Model m " +
//            "WHERE m.deleted = false " +
//            "AND m.latestVersion != 0 " +
//            "AND (:modelName IS NULL OR m.name LIKE CONCAT('%', :modelName, '%')) " +
//            "AND (:dataName IS NULL OR m.data.name = :dataName) " +
//            "AND (:userId IS NULL OR m.user.userId = :userId)")
//    Page<Model> searchMyModels(@Param("modelName") String modelName,
//                               @Param("dataName") DataSet dataName,
//                               @Param("userId") Long userId,
//                               Pageable pageable);
//
//
//    @EntityGraph(attributePaths = {"data", "user"})
//    @Query("SELECT m FROM Model m " +
//            "WHERE m.deleted = false " +
//            "AND m.latestVersion != 0" +
//            "AND (:modelName IS NULL OR m.name LIKE CONCAT('%', :modelName, '%')) " +
//            "AND (:dataName IS NULL OR m.data.name = :dataName) ")
//    Page<Model> searchModels(@Param("modelName") String modelName,
//                             @Param("dataName") DataSet dataName,
//                             Pageable pageable);
}

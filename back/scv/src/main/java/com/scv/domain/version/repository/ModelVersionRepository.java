package com.scv.domain.version.repository;

import com.scv.domain.version.domain.ModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelVersionRepository extends JpaRepository<ModelVersion, Long> {
}

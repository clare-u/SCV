package com.scv.domain.model.repository;

import com.scv.domain.data.domain.Data;
import com.scv.domain.model.domain.Model;
import com.scv.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    Page<Model> findAllByDeletedFalse(Pageable pageable);

    Page<Model> findAllByDeletedFalseAndUser(Pageable pageable, User user);

    Page<Model> findAllByDeletedFalseAndData(Pageable pageable, Data data);
}

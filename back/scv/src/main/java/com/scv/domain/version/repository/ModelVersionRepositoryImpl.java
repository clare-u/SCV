package com.scv.domain.version.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scv.domain.data.enums.DataSet;
import com.scv.domain.version.domain.ModelVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.scv.domain.model.domain.QModel.model;
import static com.scv.domain.version.domain.QModelVersion.modelVersion;

@RequiredArgsConstructor
public class ModelVersionRepositoryImpl implements ModelVersionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ModelVersion> findAllByModelIdAndDeletedFalse(Long id) {
        return queryFactory
                .selectFrom(modelVersion)
                .leftJoin(modelVersion.result).fetchJoin()
                .where(
                        modelVersion.model.id.eq(id),
                        modelVersion.deleted.isFalse()
                )
                .fetch();
    }

    @Override
    public Page<ModelVersion> findAllByUserAndIsWorkingTrueAndDeletedFalse(
            String modelName, DataSet dataName, Long userId, Pageable pageable) {
        JPAQuery<ModelVersion> query = queryFactory
                .selectFrom(modelVersion)
                .leftJoin(modelVersion.model, model).fetchJoin()
                .leftJoin(model.data).fetchJoin()
                .leftJoin(modelVersion.result).fetchJoin()
                .where(
                        modelVersion.model.user.userId.eq(userId),
                        containsModelName(modelName),
                        containsDataName(dataName),
                        modelVersion.isWorkingOn.isTrue(),
                        modelVersion.deleted.isFalse()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            applySort(query, pageable);
        }

        JPAQuery<Long> countQuery = queryFactory
                .select(modelVersion.count())
                .from(modelVersion)
                .where(
                        modelVersion.model.user.userId.eq(userId),
                        containsModelName(modelName),
                        containsDataName(dataName),
                        modelVersion.isWorkingOn.isTrue(),
                        modelVersion.deleted.isFalse()
                );

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

    @Override
    public void softDeleteAllByModelId(Long modelId) {
        queryFactory
                .update(modelVersion)
                .set(modelVersion.deleted, true)
                .where(
                        modelVersion.model.id.eq(modelId),
                        modelVersion.deleted.isFalse()
                )
                .execute();
    }

    @Override
    public void softDeleteById(Long modelVersionId) {
        queryFactory
                .update(modelVersion)
                .set(modelVersion.deleted, true)
                .where(modelVersion.id.eq(modelVersionId))
                .execute();
    }

    private void applySort(JPAQuery<?> query, Pageable pageable) {
        pageable.getSort().forEach(order -> {
            switch (order.getProperty()) {
                case "createdAt" ->
                        query.orderBy(order.isAscending() ? modelVersion.createdAt.asc() : modelVersion.createdAt.desc());
                case "updatedAt" ->
                        query.orderBy(order.isAscending() ? modelVersion.updatedAt.asc() : modelVersion.updatedAt.desc());
                default -> query.orderBy(modelVersion.createdAt.desc());
            }
        });
    }

    private BooleanExpression containsModelName(String modelName) {
        return StringUtils.hasText(modelName) ? model.name.like("%" + modelName + "%") : null;
    }

    private BooleanExpression containsDataName(DataSet dataName) {
        return dataName != null ? model.data.name.stringValue().containsIgnoreCase(dataName.getName()) : null;
    }
}

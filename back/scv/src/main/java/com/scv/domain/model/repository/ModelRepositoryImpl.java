package com.scv.domain.model.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scv.domain.data.enums.DataSet;
import com.scv.domain.model.dto.response.ModelResponse;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import static com.scv.domain.model.domain.QModel.model;
import static com.scv.domain.user.domain.QUser.user;
import static com.scv.domain.version.domain.QModelVersion.modelVersion;

@RequiredArgsConstructor
public class ModelRepositoryImpl implements ModelRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ModelResponse> searchModels(String modelName, DataSet dataName, Pageable pageable) {
        JPAQuery<ModelResponse> query = queryFactory
                .select(Projections.constructor(ModelResponse.class,
                        Projections.constructor(UserProfileResponseDTO.class,
                                user.userId,
                                user.userEmail,
                                user.userImageUrl,
                                user.userNickname,
                                user.userRepo),
                        model.id,
                        model.name,
                        model.data.name,
                        model.latestVersion,
                        model.accuracy,
                        model.createdAt,
                        model.updatedAt,
                        JPAExpressions
                                .select(modelVersion.id)
                                .from(modelVersion)
                                .where(
                                        modelVersion.model.eq(model),
                                        modelVersion.deleted.eq(false),
                                        modelVersion.versionNo.eq(model.latestVersion)
                                )
                ))
                .from(model)
                .innerJoin(model.user, user)
                .where(
                        isNotDeleted(),
                        hasLatestVersion(),
                        containsModelName(modelName),
                        containsDataName(dataName)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            applySort(query, pageable);
        }

        JPAQuery<Long> countQuery = queryFactory
                .select(model.count())
                .from(model)
                .where(
                        isNotDeleted(),
                        hasLatestVersion(),
                        containsModelName(modelName),
                        containsDataName(dataName)
                );

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ModelResponse> searchMyModels(String modelName, DataSet dataName, Long userId, Pageable pageable) {
        JPAQuery<ModelResponse> query = queryFactory
                .select(Projections.constructor(ModelResponse.class,
                        Projections.constructor(UserProfileResponseDTO.class,
                                user.userId,
                                user.userEmail,
                                user.userImageUrl,
                                user.userNickname,
                                user.userRepo),
                        model.id,
                        model.name,
                        model.data.name,
                        model.latestVersion,
                        model.accuracy,
                        model.createdAt,
                        model.updatedAt,
                        JPAExpressions
                                .select(modelVersion.id)
                                .from(modelVersion)
                                .where(
                                        modelVersion.model.eq(model),
                                        modelVersion.deleted.eq(false),
                                        modelVersion.versionNo.eq(model.latestVersion)
                                )
                ))
                .from(model)
                .innerJoin(model.user, user)
                .where(
                        isNotDeleted(),
                        hasLatestVersion(),
                        containsModelName(modelName),
                        containsDataName(dataName),
                        equalsUserId(userId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            applySort(query, pageable);
        }

        JPAQuery<Long> countQuery = queryFactory
                .select(model.count())
                .from(model)
                .where(
                        isNotDeleted(),
                        hasLatestVersion(),
                        containsModelName(modelName),
                        containsDataName(dataName),
                        equalsUserId(userId)
                );

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

    private void applySort(JPAQuery<?> query, Pageable pageable) {
        pageable.getSort().forEach(order -> {
            switch (order.getProperty()) {
                case "createdAt" -> query.orderBy(order.isAscending() ? model.createdAt.asc() : model.createdAt.desc());
                case "updatedAt" -> query.orderBy(order.isAscending() ? model.updatedAt.asc() : model.updatedAt.desc());
                default -> query.orderBy(model.createdAt.desc());
            }
        });
    }

    private BooleanExpression isNotDeleted() {
        return model.deleted.eq(false);
    }

    private BooleanExpression hasLatestVersion() {
        return model.latestVersion.ne(0);
    }

    private BooleanExpression containsModelName(String modelName) {
        return StringUtils.hasText(modelName) ? model.name.like("%" + modelName + "%") : null;
    }

    private BooleanExpression containsDataName(DataSet dataName) {
        return dataName != null ? model.data.name.stringValue().eq(dataName.getName()) : null;
    }

    private BooleanExpression equalsUserId(Long userId) {
        return userId != null ? model.user.userId.eq(userId) : null;
    }
}
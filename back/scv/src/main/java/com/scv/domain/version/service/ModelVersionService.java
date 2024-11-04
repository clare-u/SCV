package com.scv.domain.version.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import com.scv.domain.model.domain.Model;
import com.scv.domain.model.exception.ModelNotFoundException;
import com.scv.domain.model.repository.ModelRepository;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.domain.version.dto.layer.LayerDTO;
import com.scv.domain.version.dto.request.ModelVersionRequest;
import com.scv.domain.version.dto.response.ModelVersionDetail;
import com.scv.domain.version.dto.response.ModelVersionResponse;
import com.scv.domain.version.exception.ModelVersionNotFoundException;
import com.scv.domain.version.repository.ModelVersionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ModelVersionService {


    private final ModelRepository modelRepository;
    private final ModelVersionRepository modelVersionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            .enable(SerializationFeature.INDENT_OUTPUT);

    public String convertToJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    // 모델 버전 생성
    public void createModelVersion(Long modelId, ModelVersionRequest request, CustomOAuth2User user) throws BadRequestException {
        Model model = modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);

        if (user.getUserId() != model.getUser().getUserId()) {
            throw new BadRequestException("모델의 제작자만 생성할 수 있습니다.");
        }

        // JSON으로 변환
        String layersJson;
        try {
            layersJson = objectMapper.writeValueAsString(request.layers());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }

        ModelVersion modelVersion = ModelVersion.builder()
                .model(model)
                .versionNo(request.versionNo())
                .layers(layersJson)
                .build();

        modelVersionRepository.save(modelVersion);
    }


    // 모델버전 상세 조회
    public ModelVersionDetail getModelVersion(Long versionId) {
        ModelVersion version = modelVersionRepository.findById(versionId).orElseThrow(ModelVersionNotFoundException::new);
        //TODO 결과 만들면 보여주기
        return new ModelVersionDetail(version);
    }

    // 개발중인 모델 조회
    public Page<ModelVersionResponse> getModelVersionsOnWorking(CustomOAuth2User user, Pageable pageable) {
        Page<ModelVersion> modelVersions = modelVersionRepository.findAllByUserAndIsWorkingTrueAndDeletedFalse(user.getUserId(), pageable);
        return modelVersions.map(ModelVersionResponse::new);
    }

    // 모델 버전 수정
    public void updateModelVersion(Long modelVersionId, ModelVersionRequest request, CustomOAuth2User user) throws BadRequestException {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId).orElseThrow(ModelVersionNotFoundException::new);
        if (user.getUserId() != modelVersion.getModel().getUser().getUserId()) {
            throw new BadRequestException("제작자만 수정할 수 있습니다.");
        }

        // JSON으로 변환
        String layersJson;
        try {
            layersJson = objectMapper.writeValueAsString(request.layers());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }

        modelVersion.updateLayers(layersJson);  // JSON 문자열로 업데이트
        modelVersionRepository.save(modelVersion);
    }

    // 모델 버전 삭제
    public void deleteModelVersion(Long modelVersionId, CustomOAuth2User user) throws BadRequestException {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId).orElseThrow(ModelVersionNotFoundException::new);
        if (user.getUserId() != modelVersion.getModel().getUser().getUserId()) {
            throw new BadRequestException("제작자만 삭제할 수 있습니다.");
        }

        modelVersion.delete();
        // TODO 최신버전 삭제하면 Model에 최신버전 변경하기
        modelVersionRepository.save(modelVersion);
    }
    
    //TODO 결과 저장

}

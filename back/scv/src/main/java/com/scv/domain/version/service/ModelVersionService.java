package com.scv.domain.version.service;

import com.scv.domain.model.domain.Model;
import com.scv.domain.model.exception.ModelNotFoundException;
import com.scv.domain.model.repository.ModelRepository;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.version.domain.ModelVersion;
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

    // 모델 생성
    public void createModelVersion(ModelVersionRequest request) {
        Model model = modelRepository.findById(request.modelId()).orElseThrow(ModelNotFoundException::new);

        ModelVersion modelVersion = ModelVersion.builder()
                .model(model)
                .versionNo(request.versionNo())
                .layers(request.layers().toString()) 
                .build();

        modelVersionRepository.save(modelVersion);
    }


    // 모델버전 상세 조회
    public ModelVersionDetail getModelVersion(Long versionId) {
        ModelVersion version = modelVersionRepository.findById(versionId).orElseThrow(ModelVersionNotFoundException::new);

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
        //TODO Layer JSON 처리 확인하기
        modelVersion.updateLayers(request.layers().toString());

        modelVersionRepository.save(modelVersion);
    }

    // 모델 버전 삭제
    public void deleteModelVersion(Long modelVersionId, CustomOAuth2User user) throws BadRequestException {
        ModelVersion modelVersion = modelVersionRepository.findByIdAndDeletedFalse(modelVersionId).orElseThrow(ModelVersionNotFoundException::new);
        if (user.getUserId() != modelVersion.getModel().getUser().getUserId()) {
            throw new BadRequestException("제작자만 삭제할 수 있습니다.");
        }

        modelVersion.delete();
        // TODO 최신버전 삭제하면 Model에 최신버전 변경하기
        modelVersionRepository.save(modelVersion);
    }


}

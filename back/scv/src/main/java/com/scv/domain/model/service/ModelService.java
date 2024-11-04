package com.scv.domain.model.service;

import com.scv.domain.data.domain.Data;
import com.scv.domain.data.exception.DataNotFoundException;
import com.scv.domain.data.repository.DataRepository;
import com.scv.domain.model.domain.Model;
import com.scv.domain.model.dto.request.ModelCreateRequest;
import com.scv.domain.model.dto.response.ModelResponse;
import com.scv.domain.model.exception.ModelNotFoundException;
import com.scv.domain.model.repository.ModelRepository;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.exception.UserNotFoundException;
import com.scv.domain.user.repository.UserRepository;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.domain.version.dto.response.ModelVersionResponse;
import com.scv.domain.version.repository.ModelVersionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ModelService {

    private final ModelRepository modelRepository;
    private final ModelVersionRepository modelVersionRepository;
    private final DataRepository dataRepository;
    private final UserRepository userRepository;

    // TODO ModelResponse에 정확도 추가하기

    // 모델 생성
    @Transactional
    public void createModel(ModelCreateRequest request, CustomOAuth2User user) {
        Data data = dataRepository.findByName(request.dataName()).orElseThrow(DataNotFoundException::new);
        User existingUser = userRepository.findById(user.getUserId()).orElseThrow(UserNotFoundException::getInstance);

        // 요청에 따라 모델 생성
        Model model = Model.builder()
                .user(existingUser)
                .data(data)
                .name(request.modelName())
//                .latestVersion(1)
                .modelVersions(new ArrayList<>())
                .build();

        // 모델 저장
        Model savedModel = modelRepository.save(model);

        // 첫번째 버전 생성
        ModelVersion firstVersion = ModelVersion.builder()
                .model(savedModel)
                .versionNo(1)
                .layers("[]")
                .build();

        // 모델에 추가
        savedModel.getModelVersions().add(firstVersion);

        // 모델 버전 저장
        modelVersionRepository.save(firstVersion);
    }


    // 내 모델 조회
    @Transactional(readOnly = true)
    public Page<ModelResponse> getMyModels(Pageable pageable, CustomOAuth2User user, String dataName, String modelName) {
        modelName = (modelName == null || modelName.isEmpty()) ? null : modelName;
        dataName = (dataName == null || dataName.isEmpty()) ? null : dataName;

        Long userId = user.getUserId();
        Page<Model> models = modelRepository.searchMyModels(modelName, dataName, userId, pageable);
        return models.map(ModelResponse::new);
    }

    // 전체 모델 조회
    @Transactional(readOnly = true)
    public Page<ModelResponse> getAllModels(Pageable pageable, String dataName, String modelName) {
        modelName = (modelName == null || modelName.isEmpty()) ? null : modelName;
        dataName = (dataName == null || dataName.isEmpty()) ? null : dataName;
        Page<Model> models = modelRepository.searchModels(modelName, dataName, pageable);
        return models.map(ModelResponse::new);
    }

    // 모델 버전 조회
    @Transactional(readOnly = true)
    public List<ModelVersionResponse> getModelVersions(Long modelId) {
        List<ModelVersion> modelVersions = modelVersionRepository.findAllByModel_IdAndDeletedFalse(modelId);

        return modelVersions.stream()
                .map(ModelVersionResponse::new)
                .collect(Collectors.toList());
    }


    // 이름 수정
    @Transactional
    public void updateModelName(Long modelId, String name, CustomOAuth2User user) throws BadRequestException {
        Model model = modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);

        if (user.getUserId() != model.getUser().getUserId()) {
            throw new BadRequestException("자신의 모델만 수정할 수 있습니다.");
        }

        model.updateName(name);

        modelRepository.save(model);
    }


    // 모델 삭제
    @Transactional
    public void deleteModel(Long modelId, CustomOAuth2User user) throws BadRequestException {
        Model model = modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);
        if (user.getUserId() != model.getUser().getUserId()) {
            throw new BadRequestException("자신의 모델만 수정할 수 있습니다.");
        }

        List<ModelVersion> modelVersionsList = modelVersionRepository.findAllByModel_IdAndDeletedFalse(modelId);

        for (ModelVersion modelVersion : modelVersionsList) {
            modelVersion.delete();
            modelVersionRepository.save(modelVersion);
        } // 소프트 딜리트
        model.delete();

        modelRepository.save(model);
    }


}

package com.scv.domain.model.service;

import com.scv.domain.data.domain.Data;
import com.scv.domain.data.enums.DataSet;
import com.scv.domain.data.exception.DataNotFoundException;
import com.scv.domain.data.repository.DataRepository;
import com.scv.domain.model.domain.Model;
import com.scv.domain.model.dto.request.ModelCreateRequest;
import com.scv.domain.model.dto.response.ModelCreateResponse;
import com.scv.domain.model.dto.response.ModelDetailResponse;
import com.scv.domain.model.dto.response.ModelResponse;
import com.scv.domain.model.exception.ModelNotFoundException;
import com.scv.domain.model.repository.ModelRepository;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.domain.user.domain.User;
import com.scv.domain.user.exception.UserNotFoundException;
import com.scv.domain.user.repository.UserRepository;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.domain.version.repository.ModelVersionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class ModelService {

    private final ModelRepository modelRepository;
    private final ModelVersionRepository modelVersionRepository;
    private final DataRepository dataRepository;
    private final UserRepository userRepository;

    // 모델 생성
    public ModelCreateResponse createModel(ModelCreateRequest request, CustomOAuth2User user) {
        Data data = dataRepository.findByName(request.dataName()).orElseThrow(DataNotFoundException::new);
        User existingUser = userRepository.findById(user.getUserId()).orElseThrow(UserNotFoundException::getInstance);

        Model model = Model.builder()
                .user(existingUser)
                .data(data)
                .latestVersion(0)
                .name(request.modelName())
                .modelVersions(new ArrayList<>())
                .build();

        Model savedModel = modelRepository.save(model);

        ModelVersion firstVersion = ModelVersion.builder()
                .model(savedModel)
                .layers("[]")
                .versionNo(0)
                .build();

        savedModel.getModelVersions().add(firstVersion);

        modelVersionRepository.save(firstVersion);

        return new ModelCreateResponse(savedModel.getId(), firstVersion.getId());
    }

    
    // 전체 모델 조회
    @Transactional(readOnly = true)
    public Page<ModelResponse> getAllModels(Pageable pageable, DataSet dataName, String modelName) {
        return modelRepository.searchModels(modelName, dataName, pageable);
    }


    // 내 모델 조회
    @Transactional(readOnly = true)
    public Page<ModelResponse> getMyModels(Pageable pageable, CustomOAuth2User user, DataSet dataName, String modelName) {
        return modelRepository.searchMyModels(modelName, dataName, user.getUserId(), pageable);
    }


    // 모델 버전 조회
    @Transactional(readOnly = true)
    public ModelDetailResponse getModelVersions(Long modelId) {
        Model model = modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);

        return new ModelDetailResponse(model);
    }


    // 이름 수정
    public void updateModelName(Long modelId, String name, CustomOAuth2User user) throws BadRequestException {
        Model model = modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);

        if (user.getUserId() != model.getUser().getUserId()) {
            throw new BadRequestException("자신의 모델만 수정할 수 있습니다.");
        }

        model.updateName(name);

        modelRepository.save(model);
    }


    // 모델 삭제
    public void deleteModel(Long modelId, CustomOAuth2User user) throws BadRequestException {
        Model model = modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);

        if (!user.getUserId().equals(model.getUser().getUserId())) {
            throw new BadRequestException("자신의 모델만 수정할 수 있습니다.");
        }

        modelVersionRepository.softDeleteAllByModelId(modelId);

        model.delete();
        modelRepository.save(model);
    }


}

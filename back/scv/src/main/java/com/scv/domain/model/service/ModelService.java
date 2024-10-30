package com.scv.domain.model.service;

import com.scv.domain.data.domain.Data;
import com.scv.domain.data.exception.DataNotFoundException;
import com.scv.domain.data.repository.DataRepository;
import com.scv.domain.model.domain.Model;
import com.scv.domain.model.dto.request.ModelCreateRequest;
import com.scv.domain.model.dto.response.ModelResponse;
import com.scv.domain.model.exception.ModelNotFoundException;
import com.scv.domain.model.repository.ModelRepository;
import com.scv.domain.user.domain.User;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.domain.version.repository.ModelVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ModelService {

    private final ModelRepository modelRepository;
    private final ModelVersionRepository modelVersionRepository;
    private final DataRepository dataRepository;

    public void createModel(ModelCreateRequest request, User user) {
        Data data = dataRepository.findByName(request.dataName()).orElseThrow(DataNotFoundException::new);

        // 요청에 따라 모델 생성
        Model model = Model.builder()
                .user(user)
                .data(data)
                .name(request.modelName())
                .latestVersion(1)
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

    public Page<ModelResponse> findAllModels(Pageable pageable) {
        Page<Model> models = modelRepository.findAllByDeletedFalse(pageable);
        // TODO 최신버전 = run 된 버전만. modelResponse에 정확도 표시
        return models.map(ModelResponse::new);
    }

    @Transactional
    public void deleteModel(Long modelId) {
        Model model = modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);
        List<ModelVersion> modelVersionsList = modelVersionRepository.findAllByModel_Id(modelId);

        for (ModelVersion modelVersion : modelVersionsList) {
            modelVersion.delete();
            modelVersionRepository.save(modelVersion);
        } // 소프트 딜리트

        model.delete();

        modelRepository.save(model);
    }


}

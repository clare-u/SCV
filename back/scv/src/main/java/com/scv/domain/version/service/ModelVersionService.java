package com.scv.domain.version.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.scv.domain.data.domain.Data;
import com.scv.domain.data.enums.DataSet;
import com.scv.domain.data.exception.DataNotFoundException;
import com.scv.domain.data.repository.DataRepository;
import com.scv.domain.model.domain.Model;
import com.scv.domain.model.exception.ModelNotFoundException;
import com.scv.domain.model.repository.ModelRepository;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.result.domain.Result;
import com.scv.domain.result.dto.request.ResultRequest;
import com.scv.domain.result.dto.response.ResultAnalysisResponse;
import com.scv.domain.result.exception.ResultNotFoundException;
import com.scv.domain.result.repository.ResultRepository;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.domain.version.dto.request.ModelVersionRequest;
import com.scv.domain.version.dto.response.ModelVersionDetail;
import com.scv.domain.version.dto.response.ModelVersionDetailWithResult;
import com.scv.domain.version.dto.response.ModelVersionOnWorking;
import com.scv.domain.version.dto.response.ModelVersionResponse;
import com.scv.domain.version.exception.ModelVersionNotFoundException;
import com.scv.domain.version.repository.ModelVersionRepository;
import com.scv.global.util.ParsingUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ModelVersionService {

    private final ModelRepository modelRepository;
    private final ModelVersionRepository modelVersionRepository;
    private final ResultRepository resultRepository;
    private final DataRepository dataRepository;

    // 모델 버전 생성
    public void createModelVersion(Long modelId, ModelVersionRequest request, CustomOAuth2User user) throws BadRequestException {
        Model model = modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);

        if (user.getUserId() != model.getUser().getUserId()) {
            throw new BadRequestException("모델의 제작자만 생성할 수 있습니다.");
        }

        String layersJson = ParsingUtil.toJson(request.layers());

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

        Optional<Result> result = resultRepository.findById(versionId);
        if (result.isPresent()) {
            ResultAnalysisResponse resultAnalysisResponse = new ResultAnalysisResponse(result.get());
            return new ModelVersionDetailWithResult(version, resultAnalysisResponse);
        }

        return new ModelVersionDetail(version);
    }

    // 개발중인 모델 조회
    public Page<ModelVersionOnWorking> getModelVersionsOnWorking(CustomOAuth2User user, Pageable pageable) {
        Page<ModelVersion> modelVersions = modelVersionRepository.findAllByUserAndIsWorkingTrueAndDeletedFalse(user.getUserId(), pageable);

        return modelVersions.map(ModelVersionOnWorking::new);
    }

    // 모델 버전 수정
    public void updateModelVersion(Long modelVersionId, ModelVersionRequest request, CustomOAuth2User user) throws BadRequestException {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(ModelVersionNotFoundException::new);

        // 사용자 권한 검사
        if (user.getUserId() != modelVersion.getModel().getUser().getUserId()) {
            throw new BadRequestException("제작자만 수정할 수 있습니다.");
        }

        String layersJson = ParsingUtil.toJson(request.layers());

        // 모델 버전 정보 업데이트
        modelVersion.updateVersionNo(request.versionNo());
        modelVersion.updateLayers(layersJson);

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

    // 이건 결과저장
    public void saveResult(Long modelVersionId, DataSet dataName) {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(ModelVersionNotFoundException::new);
        Data data = dataRepository.findByName(dataName).orElseThrow(DataNotFoundException::new);

        String url = "http://localhost:8002/fast/v1/model/test/analyze/" + 0 + "/" + dataName.toString().toLowerCase();
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("layers", ParsingUtil.toJson(modelVersion.getLayers()));
        jsonMap.put("dataName", dataName.toString());
        jsonMap.put("dataTrainCnt", data.getTrainCnt());
        jsonMap.put("dataTestCnt", data.getTestCnt());
        jsonMap.put("dataLabelCnt", data.getLabelCnt());
        jsonMap.put("dataEpochCnt", data.getEpochCnt());

        String jsonData = ParsingUtil.toJson(jsonMap);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        String jsonResponse = response.getBody();

        JsonNode rootNode = ParsingUtil.parseJson(jsonResponse, JsonNode.class);

        String codeView = ParsingUtil.getJsonFieldAsString(rootNode, "code");
        String confusionMatrix = ParsingUtil.getJsonFieldAsString(rootNode, "confusion_matrix");
        String activationMaximization = ParsingUtil.getJsonFieldAsString(rootNode, "activation_maximization");
        String featureActivation = ParsingUtil.getJsonFieldAsString(rootNode, "feature_activation");
        String exampleImage = ParsingUtil.getJsonFieldAsString(rootNode, "example_image");
        String trainInfoJson = ParsingUtil.getJsonFieldAsString(rootNode, "train_info");
        String params = ParsingUtil.getJsonFieldAsString(rootNode, "params");

        Result result = Result.builder()
                .modelVersion(modelVersion)
                .code(codeView)
                .testAccuracy(rootNode.path("test_accuracy").asDouble())
                .testLoss(rootNode.path("test_loss").asDouble())
                .trainInfo(trainInfoJson)
                .confusionMatrix(confusionMatrix)
                .exampleImg(exampleImage)
                .totalParams(rootNode.path("totalParams").asInt())
                .params(params)
                .featureActivation(featureActivation)
                .activationMaximization(activationMaximization)
                .build();

        resultRepository.save(result);
    }

    // TODO 개판이니까 리팩토링 필요,,,,,, 다른 얽힌 메서드들 추가하기
    // 실핸하기
    public void runResult(Long modelVersionId, DataSet dataName, ResultRequest request) {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(ModelVersionNotFoundException::new);
        Result result = resultRepository.findById(modelVersionId)
                .orElseThrow(ResultNotFoundException::new);

        String url = "http://localhost:8002/fast/v1/model/test/analyze/" + modelVersionId + "/" + dataName;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonResponse = response.getBody();

        ResultAnalysisResponse resultResponse = ParsingUtil.parseJson(jsonResponse, ResultAnalysisResponse.class);

        String trainInfoJson = ParsingUtil.toJson(resultResponse.trainInfos());

        result = result.toBuilder()
                .confusionMatrix(resultResponse.confusionMatrix())
                .activationMaximization(resultResponse.activationMaximization())
                .exampleImg(resultResponse.exampleImg())
                .featureActivation(resultResponse.featureActivation())
                .build();

        resultRepository.save(result);
    }
}
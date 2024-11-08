package com.scv.domain.version.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.scv.domain.data.domain.Data;
import com.scv.domain.data.dto.DataDTO;
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

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ModelVersionService {

    private final ModelRepository modelRepository;
    private final ModelVersionRepository modelVersionRepository;
    private final ResultRepository resultRepository;
    private final DataRepository dataRepository;

    // 모델 버전 생성
    @Transactional
    public void createModelVersion(Long modelId, ModelVersionRequest request, CustomOAuth2User user) throws BadRequestException {
        Model model = modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);

        if (user.getUserId() != model.getUser().getUserId()) {
            throw new BadRequestException("모델의 제작자만 생성할 수 있습니다.");
        }

        String layersJson = ParsingUtil.toJson(request.layers());

        ModelVersion modelVersion = ModelVersion.builder()
                .model(model)
                .versionNo(0)
                .layers(layersJson)
                .build();

        modelVersionRepository.save(modelVersion);
    }


    // 모델버전 상세 조회
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<ModelVersionOnWorking> getModelVersionsOnWorking(CustomOAuth2User user, Pageable pageable) {
        Page<ModelVersion> modelVersions = modelVersionRepository.findAllByUserAndIsWorkingTrueAndDeletedFalse(user.getUserId(), pageable);

        return modelVersions.map(ModelVersionOnWorking::new);
    }

    // 모델 버전 수정
    @Transactional
    public void updateModelVersion(Long modelVersionId, ModelVersionRequest request, CustomOAuth2User user) throws BadRequestException {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(ModelVersionNotFoundException::new);

        // 사용자 권한 검사
        if (user.getUserId() != modelVersion.getModel().getUser().getUserId()) {
            throw new BadRequestException("제작자만 수정할 수 있습니다.");
        }

        String layersJson = ParsingUtil.toJson(request.layers());

        // 모델 버전 정보 업데이트
        modelVersion.updateLayers(layersJson);
        Optional<Result> result = resultRepository.findById(modelVersionId);

        result.ifPresent(resultRepository::delete);
        modelVersionRepository.save(modelVersion);
    }


    // 모델 버전 삭제
    @Transactional
    public void deleteModelVersion(Long modelVersionId, CustomOAuth2User user) throws BadRequestException {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(ModelVersionNotFoundException::new);
        if (!user.getUserId().equals(modelVersion.getModel().getUser().getUserId())) {
            throw new BadRequestException("제작자만 삭제할 수 있습니다.");
        }

        Model model = modelVersion.getModel();
        modelVersion.delete();
        modelVersionRepository.save(modelVersion);

        // 버전, 정확도관리
        if (model.getLatestVersion() != 0) {
            List<ModelVersion> modelVersionList = modelVersionRepository.findAllByModel_IdAndDeletedFalse(model.getId());
            modelVersionList.sort(Comparator.comparingInt(ModelVersion::getVersionNo).reversed());

            if (!modelVersionList.isEmpty()) {
                for (int i = 0; i < modelVersionList.size(); i++) {
                    if (modelVersionList.get(i).getResult() != null) {
                        model.setAccuracy(modelVersionList.get(i).getResult().getTestAccuracy());
                        model.setLatestVersion(modelVersionList.get(i).getVersionNo());
                        break;
                    }
                }
            } else {
                model.setLatestVersion(0);
                model.setAccuracy(-1.0);
            }
        }
        modelRepository.save(model);
    }

    //    실행하기
    @Transactional
    public String runResult(Long modelVersionId) {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(ModelVersionNotFoundException::new);
        Data data = dataRepository.findById(modelVersion.getModel().getData().getId()).orElseThrow(DataNotFoundException::new);

        ResultRequest request = new ResultRequest(modelVersion.getLayers(), new DataDTO(data));

        String url = "http://localhost:8002/fast/v1/models/" + modelVersion.getModel().getId() + "/versions/" + modelVersionId;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        String jsonResponse = response.getBody();

        // JSON 응답을 파싱하여 필요한 데이터 추출
        JsonNode rootNode = ParsingUtil.parseJson(jsonResponse, JsonNode.class);
        JsonNode testResults = rootNode.path("test_results").path("results");

        // 필요한 필드 추출
        double finalTestAccuracy = testResults.path("final_test_accuracy").asDouble(0.0);
        double finalTestLoss = testResults.path("final_test_loss").asDouble(0.0);
        String modelCode = testResults.path("model_code").asText("");

        // 레이어 파라미터의 합계 계산
        int totalParams = 0;
        for (JsonNode paramNode : testResults.path("layer_parameters")) {
            totalParams += paramNode.asInt(0);
        }

        // 에포크별 학습 결과 추출
        String trainInfo = testResults.path("train_result_per_epoch").toString();

        // Result 객체 생성
        Result result = Result.builder()
                .modelVersion(modelVersion)
                .code(modelCode)
                .testAccuracy(finalTestAccuracy)
                .testLoss(finalTestLoss)
                .trainInfo(trainInfo)
                .params(testResults.path("layer_parameters").toString())
                .totalParams(totalParams)
                .build();

        // 데이터베이스에 저장
        resultRepository.save(result);

        return jsonResponse;
    }

    // 결과저장
    @Transactional
    public void saveResult(Long modelVersionId, DataSet dataName) {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(ModelVersionNotFoundException::new);
        Result result = resultRepository.findById(modelVersionId).orElseThrow(ResultNotFoundException::new);
        String data = dataName.toString();
        if (data.equals("Fashion")) {
            data += "_MNIST";
        }

        String url = "http://localhost:8002/fast/v1/model/test/analyze/" + modelVersionId + "/" + data.toLowerCase();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonResponse = response.getBody();

        JsonNode rootNode = ParsingUtil.parseJson(jsonResponse, JsonNode.class);

        String codeView = ParsingUtil.getJsonFieldAsString(rootNode, "code");
        String confusionMatrix = ParsingUtil.getJsonFieldAsString(rootNode, "confusion_matrix");
        String activationMaximization = ParsingUtil.getJsonFieldAsString(rootNode, "activation_maximization");
        String featureActivation = ParsingUtil.getJsonFieldAsString(rootNode, "feature_activation");
        String exampleImage = ParsingUtil.getJsonFieldAsString(rootNode, "example_image");
        String trainInfoJson = ParsingUtil.getJsonFieldAsString(rootNode, "train_info");
        String params = ParsingUtil.getJsonFieldAsString(rootNode, "params");

        result = result.toBuilder()
//                .modelVersion(modelVersion)
//                .code(codeView)
//                .testAccuracy(rootNode.path("test_accuracy").asDouble())
//                .testLoss(rootNode.path("test_loss").asDouble())
//                .trainInfo(trainInfoJson)
                .confusionMatrix(confusionMatrix)
                .exampleImg(exampleImage)
//                .totalParams(rootNode.path("totalParams").asInt())
//                .params(params)
                .featureActivation(featureActivation)
                .activationMaximization(activationMaximization)
                .build();

        resultRepository.save(result);

        Model model = modelVersion.getModel();
        int latest = model.getLatestVersion();

        modelVersion.updateVersionNo(latest + 1);
        modelVersion.toggleWork();
        modelVersionRepository.save(modelVersion);

        model.setLatestVersion(latest + 1);
        modelRepository.save(model);
    }


}
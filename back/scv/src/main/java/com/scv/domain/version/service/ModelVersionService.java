package com.scv.domain.version.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import com.scv.domain.result.dto.response.ResultAnalyzeResponse;
import com.scv.domain.result.exception.ResultNotFoundException;
import com.scv.domain.result.repository.ResultRepository;
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

@Service
@RequiredArgsConstructor
@Transactional
public class ModelVersionService {

    private final ModelRepository modelRepository;
    private final ModelVersionRepository modelVersionRepository;
    private final ResultRepository resultRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            .enable(SerializationFeature.INDENT_OUTPUT);
    private final DataRepository dataRepository;

    private String convertToJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }
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
        // TODO ModelResponse로 주기
        return modelVersions.map(ModelVersionResponse::new);
    }

    // 모델 버전 수정
    public void updateModelVersion(Long modelVersionId, ModelVersionRequest request, CustomOAuth2User user) throws BadRequestException {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId).orElseThrow(ModelVersionNotFoundException::new);
        if (user.getUserId() != modelVersion.getModel().getUser().getUserId()) {
            throw new BadRequestException("제작자만 수정할 수 있습니다.");
        }

        // TODO TEST해보기
        String layersJson = convertToJson(request.layers());

        modelVersion.updateVersionNo(request.versionNo());
        modelVersion.updateLayers(layersJson); // TODO 수정
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


    @Transactional
    public void saveResult(Long modelVersionId, DataSet dataName) {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(ModelVersionNotFoundException::new);
        Data data = dataRepository.findByName(dataName).orElseThrow(DataNotFoundException::new);

        String url = "http://localhost:8002/fast/v1/model/test/analyze/" + 0 + "/" + dataName.toString().toLowerCase();
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> jsonMap = new HashMap<>();
        String layersAsString;
        try {
            layersAsString = objectMapper.writeValueAsString(objectMapper.readValue(modelVersion.getLayers(), List.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process layers JSON", e);
        }
        jsonMap.put("layers", layersAsString);
        jsonMap.put("dataName", dataName.toString());
        jsonMap.put("dataTrainCnt", data.getTrainCnt());
        jsonMap.put("dataTestCnt", data.getTestCnt());
        jsonMap.put("dataLabelCnt", data.getLabelCnt());
        jsonMap.put("dataEpochCnt", data.getEpochCnt());

        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert request data to JSON", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        String jsonResponse = response.getBody();
        System.out.println("Response JSON: " + jsonResponse);

        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }

        // JSON 필드 직렬화 및 기본값 설정
        String codeView = getJsonFieldAsString(objectMapper, rootNode, "code");
        String confusionMatrix = getJsonFieldAsString(objectMapper, rootNode, "confusion_matrix");
        String activationMaximization = getJsonFieldAsString(objectMapper, rootNode, "activation_maximization");
        String featureActivation = getJsonFieldAsString(objectMapper, rootNode, "feature_activation");
        String exampleImage = getJsonFieldAsString(objectMapper, rootNode, "example_image");
        String trainInfoJson = getJsonFieldAsString(objectMapper, rootNode, "train_info");
        String layerParams = getJsonFieldAsString(objectMapper, rootNode, "layer_params");
        String params = getJsonFieldAsString(objectMapper, rootNode, "params");

        Result result = Result.builder()
                .modelVersion(modelVersion)
                .code(codeView)
                .testAccuracy(rootNode.path("test_accuracy").asDouble())
                .testLoss(rootNode.path("test_loss").asDouble())
                .trainInfo(trainInfoJson)
                .confusionMatrix(confusionMatrix)
                .exampleImg(exampleImage)
                .totalParams(rootNode.path("totalParams").asInt())
                .layerParams(layerParams)
                .params(params)
                .featureActivation(featureActivation)
                .activationMaximization(activationMaximization)
                .build();

        resultRepository.save(result);
    }
    // TODO 개판이니까 리팩토링 필요,,,,,, 다른 얽힌 메서드들 추가하기
    // 저장 했을 때 필요한 서비스
    @Transactional
    public void saveAnalysis(Long modelVersionId, DataSet dataName, ResultRequest request) {
        ModelVersion modelVersion = modelVersionRepository.findById(modelVersionId)
                .orElseThrow(ModelVersionNotFoundException::new);
        Result result = resultRepository.findById(modelVersionId)
                .orElseThrow(ResultNotFoundException::new);

        String url = "http://localhost:8002/fast/v1/model/test/analyze/" + modelVersionId + "/" + dataName;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String jsonResponse = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        ResultAnalyzeResponse resultResponse;

        try {
            resultResponse = objectMapper.readValue(jsonResponse, ResultAnalyzeResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }

        String trainInfoJson;
        try {
            trainInfoJson = objectMapper.writeValueAsString(resultResponse.trainInfos());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert trainInfos to JSON", e);
        }

        result = result.toBuilder()
                .activationMaximization(resultResponse.activationMaximization())
                .confusionMatrix(resultResponse.ConfusionMatrix())
                .exampleImg(resultResponse.exampleImg())
                .featureActivation(resultResponse.featureActivation())
                .build();

        resultRepository.save(result);
    }

    private String getJsonFieldAsString(ObjectMapper objectMapper, JsonNode rootNode, String fieldName) {
        JsonNode fieldNode = rootNode.path(fieldName);
        try {
            return fieldNode.isMissingNode() || fieldNode.isNull() || fieldNode.toString().isEmpty() ? "{}" : objectMapper.writeValueAsString(fieldNode);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
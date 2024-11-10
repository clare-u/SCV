package com.scv.domain.model.controller;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.model.dto.request.ModelCreateRequest;
import com.scv.domain.model.dto.response.ModelResponse;
import com.scv.domain.model.service.ModelService;
import com.scv.global.oauth2.auth.AuthUser;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.domain.version.dto.response.ModelVersionResponse;
import com.scv.global.error.ErrorResponse;
import com.scv.global.util.PageableUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
@Tag(name = "모델 컨트롤러", description = "모델 관련 API")
public class ModelController {

    private final ModelService modelService;
    private final PageableUtil pageableUtil;

    @PostMapping("")
    @Operation(summary = "모델 생성", description = "새로운 모델을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "모델 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> createModel(@RequestBody ModelCreateRequest request, @AuthUser CustomOAuth2User user) {
        modelService.createModel(request, user);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{modelId}")
    @Operation(summary = "모델 삭제", description = "모델 삭제하고, 모든 모델 버전을 삭제합니다.(soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "모델 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "모델을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteModel(@PathVariable("modelId") Long modelId, @AuthUser CustomOAuth2User user) throws BadRequestException {
        modelService.deleteModel(modelId, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{modelId}")
    @Operation(summary = "모델 수정", description = "모델 이름을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모델 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "모델을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> updateModelName(@PathVariable("modelId") Long modelId, String newName, @AuthUser CustomOAuth2User user) throws BadRequestException {
        modelService.updateModelName(modelId, newName, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{modelId}")
    @Operation(summary = "모델의 버전들 조회", description = "모델 버전들을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모델버전 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<List<ModelVersionResponse>> getModelVersion(@PathVariable("modelId") Long modelId) {
        List<ModelVersionResponse> modelVersions = modelService.getModelVersions(modelId);
        return ResponseEntity.ok(modelVersions);
    }

    @GetMapping("/")
    @Operation(summary = "전체 모델 조회", description = "전체 모델을 조회합니다. orderBy = createdAt or updatedAt, direction = asc or desc. 미입력시 정렬 안함.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모델 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Page<ModelResponse>> getAllModels(
            @RequestParam(defaultValue = "") DataSet dataName,
            @RequestParam(defaultValue = "") String modelName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "") String orderBy,
            @RequestParam(defaultValue = "") String direction
    ) {
        Pageable pageable = pageableUtil.createPageable(page, size, orderBy, direction);
        Page<ModelResponse> pages = modelService.getAllModels(pageable, dataName, modelName);
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/users")
    @Operation(summary = "내 모델 조회", description = "내 모델을 조회합니다. orderBy = createdAt or updatedAt, direction = asc or desc. 미입력시 정렬 안함.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모델 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Page<ModelResponse>> getMyModels(
            @RequestParam(defaultValue = "") DataSet dataName,
            @RequestParam(defaultValue = "") String modelName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "") String orderBy,
            @RequestParam(defaultValue = "") String direction,
            @AuthUser CustomOAuth2User user
    ) {
        Pageable pageable = pageableUtil.createPageable(page, size, orderBy, direction);
        Page<ModelResponse> pages = modelService.getMyModels(pageable, user, dataName, modelName);
        return ResponseEntity.ok(pages);
    }
}

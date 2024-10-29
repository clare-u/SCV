package com.scv.domain.model.controller;

import com.scv.domain.model.dto.request.ModelCreateRequest;
import com.scv.domain.model.service.ModelService;
import com.scv.domain.user.domain.User;
import com.scv.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
@Tag(name = "모델 컨트롤러", description = "모델 관련 API")
public class ModelController {

    private final ModelService modelService;

    @PostMapping("")
    @Operation(summary = "모델 생성", description = "새로운 모델을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "모델 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> createModel(ModelCreateRequest request, @AuthenticationPrincipal User user) {
        modelService.createModel(request, user);
        return ResponseEntity.status(201).build();
    }

}

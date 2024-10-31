package com.scv.domain.version.controller;

import com.scv.domain.version.service.ModelVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/models/versions")
@RequiredArgsConstructor
public class ModelVersionController {

    private final ModelVersionService modelVersionService;

    // 모델 버전 생성
    
    // 모델 버전 상세 조회
    
    // 모델 버전 수정
    
    // 모델 버전 삭제


}

package com.scv.domain.user.controller;

import com.scv.domain.oauth2.AuthUser;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.user.service.UserService;
import com.scv.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "유저 컨트롤러", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "유저 프로필 조회", description = "로그인한 유저의 프로필을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 프로필 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@AuthUser CustomOAuth2User user) {
        UserProfileResponseDTO responseDTO = userService.getUserProfile(user.getUserId());
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}

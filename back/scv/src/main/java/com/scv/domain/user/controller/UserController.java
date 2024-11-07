package com.scv.domain.user.controller;

import com.scv.domain.oauth2.AuthUser;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.request.CommitGithubRepositoryFileRequestDTO;
import com.scv.domain.user.dto.request.GithubRepositoryNameRequestDTO;
import com.scv.domain.user.dto.response.GithubRepositoryNameResponseDTO;
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
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@AuthUser CustomOAuth2User authUser) {
        UserProfileResponseDTO responseDTO = userService.getUserProfile(authUser);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/repo")
    @Operation(summary = "깃허브 새 리포를 메인 리포로 설정", description = "깃허브 새 리포를 메인 리포로 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "깃허브 새 리포를 메인 리포로 설정 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<GithubRepositoryNameResponseDTO> setNewGithubRepository(@AuthUser CustomOAuth2User authUser,
                                                                                  @RequestBody GithubRepositoryNameRequestDTO requestDTO) {
        GithubRepositoryNameResponseDTO responseDTO = userService.setNewGithubRepository(authUser, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/repo")
    @Operation(summary = "깃허브 기존 리포를 메인 리포로 설정", description = "깃허브 기존 리포를 메인 리포로 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브 기존 리포를 메인 리포로 설정 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<GithubRepositoryNameResponseDTO> setCurrentGithubRepository(@AuthUser CustomOAuth2User authUser,
                                                                                      @RequestBody GithubRepositoryNameRequestDTO requestDTO) {
        GithubRepositoryNameResponseDTO responseDTO = userService.setCurrentGithubRepository(authUser, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/repo/import")
    @Operation(summary = "깃허브에서 모델 import", description = "깃허브에서 블록 json 파일을 import 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브에서 모델 import 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> importModel(@AuthUser CustomOAuth2User auth2User,
                                              @RequestParam String modelName) {
        String responseDTO = userService.importModel(auth2User, modelName);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("repo/export")
    @Operation(summary = "깃허브에 모델 export", description = "깃허브에 블록 json 파일을 export 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브에 모델 export 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> exportModel(@AuthUser CustomOAuth2User auth2User,
                                              @RequestBody CommitGithubRepositoryFileRequestDTO requestDTO) {
        String responseDTO = userService.exportModel(auth2User, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}

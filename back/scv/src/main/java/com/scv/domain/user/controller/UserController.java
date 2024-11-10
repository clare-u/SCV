package com.scv.domain.user.controller;

import com.scv.domain.data.enums.DataSet;
import com.scv.global.oauth2.auth.AuthUser;
import com.scv.global.oauth2.auth.CustomOAuth2User;
import com.scv.domain.user.dto.request.CommitGithubRepoFileRequestDTO;
import com.scv.domain.user.dto.request.LinkGithubRepoRequestDTO;
import com.scv.domain.user.dto.response.GithubRepoFileResponseDTO;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.user.service.GithubService;
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
@RequestMapping("/api/v1/users")
@Tag(name = "유저 컨트롤러", description = "유저 관련 API")
public class UserController {

    private final UserService userService;
    private final GithubService githubService;

    @GetMapping
    @Operation(summary = "유저 프로필 조회", description = "로그인한 유저의 프로필을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 프로필 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@AuthUser CustomOAuth2User authUser) {
        UserProfileResponseDTO responseDTO = userService.getUserProfile(authUser);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/repo")
    @Operation(summary = "깃허브 리포 연동 해제", description = "깃허브 리포 연동을 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브 리포 연동 해제 성공"),
    })
    public ResponseEntity<Void> unLinkGithubRepo(@AuthUser CustomOAuth2User authUser) {
        userService.unLinkGithubRepo(authUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/repo")
    @Operation(summary = "깃허브 새 리포를 메인 리포로 설정", description = "깃허브 새 리포를 메인 리포로 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "깃허브 새 리포를 메인 리포로 설정 성공"),
            @ApiResponse(responseCode = "400", description = "GITHUB_API_BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "GITHUB_API_UNAUTHORIZED", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "GITHUB_API_FORBIDDEN", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "GITHUB_API_NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "GITHUB_API_CONFLICT", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "GITHUB_API_UNPROCESSABLE_ENTITY", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Void> linkNewGithubRepo(@AuthUser CustomOAuth2User authUser,
                                                  @RequestBody LinkGithubRepoRequestDTO requestDTO) {
        githubService.linkNewGithubRepo(authUser, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/repo")
    @Operation(summary = "깃허브 기존 리포를 메인 리포로 설정", description = "깃허브 기존 리포를 메인 리포로 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브 새 리포를 메인 리포로 설정 성공"),
            @ApiResponse(responseCode = "404", description = "GITHUB_API_NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Void> linkCurrentGithubRepo(@AuthUser CustomOAuth2User authUser,
                                                      @RequestBody LinkGithubRepoRequestDTO requestDTO) {
        githubService.linkCurrentGithubRepo(authUser, requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/repo/import")
    @Operation(summary = "깃허브에서 모델 import", description = "깃허브에서 블록 json 파일을 import 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브에서 모델 import 성공"),
            @ApiResponse(responseCode = "403", description = "GITHUB_API_FORBIDDEN", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "GITHUB_API_NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<GithubRepoFileResponseDTO> importGithubRepoFile(@AuthUser CustomOAuth2User auth2User,
                                                                          @RequestParam DataSet dataName,
                                                                          @RequestParam String modelName) {
        GithubRepoFileResponseDTO responseDTO = githubService.importGithubRepoFile(auth2User, dataName, modelName);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("repo/export")
    @Operation(summary = "깃허브에 모델 export", description = "깃허브에 블록 json 파일을 export 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브에 모델 export 성공"),
            @ApiResponse(responseCode = "403", description = "GITHUB_API_FORBIDDEN", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "GITHUB_API_NOT_FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "GITHUB_API_CONFLICT", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "GITHUB_API_UNPROCESSABLE_ENTITY", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Void> exportGithubRepoFile(@AuthUser CustomOAuth2User auth2User,
                                                     @RequestBody CommitGithubRepoFileRequestDTO requestDTO) {
        githubService.exportGithubRepoFile(auth2User, requestDTO);
        return ResponseEntity.ok().build();
    }

}

package com.scv.domain.user.controller;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.oauth2.AuthUser;
import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.request.CommitGithubRepositoryFileRequestDTO;
import com.scv.domain.user.dto.request.GithubRepositoryNameRequestDTO;
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
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@AuthUser CustomOAuth2User authUser) {
        UserProfileResponseDTO responseDTO = userService.getUserProfile(authUser);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/repo")
    @Operation(summary = "깃허브 새 리포를 메인 리포로 설정", description = "깃허브 새 리포를 메인 리포로 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "깃허브 새 리포를 메인 리포로 설정 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 리포입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> setNewGithubRepository(@AuthUser CustomOAuth2User authUser,
                                                       @RequestBody GithubRepositoryNameRequestDTO requestDTO) {
        boolean result = userService.setNewGithubRepository(authUser, requestDTO);

        if (result) return ResponseEntity.status(HttpStatus.CREATED).build();
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PutMapping("/repo")
    @Operation(summary = "깃허브 기존 리포를 메인 리포로 설정", description = "깃허브 기존 리포를 메인 리포로 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브 기존 리포를 메인 리포로 설정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리포입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> setCurrentGithubRepository(@AuthUser CustomOAuth2User authUser,
                                                           @RequestBody GithubRepositoryNameRequestDTO requestDTO) {
        boolean result = userService.setCurrentGithubRepository(authUser, requestDTO);

        if (result) return ResponseEntity.ok().build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/repo")
    @Operation(summary = "깃허브 리포 연동을 해제", description = "깃허브 리포 연동을 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브 리포 연동을 해제 성공"),
            @ApiResponse(responseCode = "400", description = "깃허브 리포 연동을 해제 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> disConnectGithubRepository(@AuthUser CustomOAuth2User authUser) {
        boolean result = userService.disConnectGithubRepository(authUser);

        if (result) return ResponseEntity.ok().build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/repo/import")
    @Operation(summary = "깃허브에서 모델 import", description = "깃허브에서 블록 json 파일을 import 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브에서 모델 import 성공"),
            @ApiResponse(responseCode = "404", description = "깃허브에서 모델 import 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> importModel(@AuthUser CustomOAuth2User auth2User,
                                              @RequestParam DataSet dataName,
                                              @RequestParam String modelName) {
        String responseDTO = userService.importModel(auth2User, dataName, modelName);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("repo/export")
    @Operation(summary = "깃허브에 모델 export", description = "깃허브에 블록 json 파일을 export 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "깃허브에 모델 export 성공"),
            @ApiResponse(responseCode = "404", description = "깃허브에 모델 export 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> exportModel(@AuthUser CustomOAuth2User auth2User,
                                            @RequestBody CommitGithubRepositoryFileRequestDTO requestDTO) {
        boolean result = userService.exportModel(auth2User, requestDTO);

        if (result) return ResponseEntity.ok().build();
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}

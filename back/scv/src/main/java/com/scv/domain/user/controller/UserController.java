package com.scv.domain.user.controller;

import com.scv.domain.oauth2.CustomOAuth2User;
import com.scv.domain.user.dto.response.UserProfileResponseDTO;
import com.scv.domain.user.service.UserService;
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
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@AuthenticationPrincipal CustomOAuth2User user) {
        UserProfileResponseDTO responseDTO = userService.getUserProfile(user.getUserId());
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}

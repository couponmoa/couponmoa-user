package com.couponmoa.backend.couponmoauser.domain.user.controller;

import com.couponmoa.backend.couponmoauser.common.dto.ApiResponse;
import com.couponmoa.backend.couponmoauser.domain.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/image")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> updateUserImage(
            @RequestHeader("X-User-Id") Long userId, @RequestPart(value = "image") MultipartFile image) {
        userProfileService.updateUserImage(userId, image);
        return ResponseEntity.ok(ApiResponse.success("회원 프로필 사진 등록 완료"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> updateUserImage(
            @RequestHeader("X-User-Id") Long userId) {
        userProfileService.deleteUserImage(userId);
        return ResponseEntity.ok(ApiResponse.success("회원 프로필 사진 삭제 완료"));
    }
}

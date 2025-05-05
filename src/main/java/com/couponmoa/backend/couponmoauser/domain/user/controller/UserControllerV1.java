package com.couponmoa.backend.couponmoauser.domain.user.controller;

import com.couponmoa.backend.couponmoauser.common.dto.ApiResponse;
import com.couponmoa.backend.couponmoauser.domain.user.dto.request.UserDeleteRequest;
import com.couponmoa.backend.couponmoauser.domain.user.dto.request.UserUpdatePasswordRequest;
import com.couponmoa.backend.couponmoauser.domain.user.dto.request.UserUpdateRequest;
import com.couponmoa.backend.couponmoauser.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.couponmoauser.domain.user.service.UserServiceV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserControllerV1 {

    private final UserServiceV1 userServiceV1;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> findUser(@RequestHeader("X-User-Id") Long userId) {
        UserResponse userResponse = userServiceV1.findUser(userId);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "회원 조회 완료"));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @RequestHeader("X-User-Id") Long userId, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        userServiceV1.updateUser(userId, userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 완료"));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(
            @RequestHeader("X-User-Id") Long userId, @Valid @RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
        userServiceV1.updateUserPassword(userId, userUpdatePasswordRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 수정 완료"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @RequestHeader("X-User-Id") Long userId, @Valid @RequestBody UserDeleteRequest userDeleteRequest) {
        userServiceV1.deleteUser(userId, userDeleteRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success("탈퇴 완료"));
    }
}

package com.couponmoa.backend.couponmoauser.domain.user.controller;

import com.couponmoa.backend.couponmoauser.common.service.RedisService;
import com.couponmoa.backend.couponmoauser.config.JwtUtil;
import com.couponmoa.backend.couponmoauser.config.SecurityConfig;
import com.couponmoa.backend.couponmoauser.domain.user.dto.request.UserDeleteRequest;
import com.couponmoa.backend.couponmoauser.domain.user.dto.request.UserUpdatePasswordRequest;
import com.couponmoa.backend.couponmoauser.domain.user.dto.request.UserUpdateRequest;
import com.couponmoa.backend.couponmoauser.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.couponmoauser.domain.user.entity.User;
import com.couponmoa.backend.couponmoauser.domain.user.enums.UserRole;
import com.couponmoa.backend.couponmoauser.domain.user.service.UserServiceV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerV1.class)
@AutoConfigureRestDocs
@Import({SecurityConfig.class, JwtUtil.class})
public class UserControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private UserServiceV1 userServiceV1;

    @Test
    @WithMockUser
    void 사용자_조회() throws Exception {
        //given
        long userId = 1L;
        String email = "email@email.com";
        User user = new User(email, "password", "name", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "id", userId);
        UserResponse mockResponse = UserResponse.fromEntityV1(user);
        given(userServiceV1.findUser(anyLong())).willReturn(mockResponse);

        //when&then
        mockMvc.perform(get("/api/v1/users")
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.imageUrl").value(startsWith("https://couponmoa-user-profile.s3")))
                .andDo(document("user-get",
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.id").description("사용자 ID"),
                                fieldWithPath("data.email").description("사용자 이메일"),
                                fieldWithPath("data.nickname").description("사용자 이름"),
                                fieldWithPath("data.userRole").description("사용자 권한 (ex: ROLE_USER)"),
                                fieldWithPath("data.imageUrl").description("사용자 프로필 이미지 URL (S3 경로)")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 사용자_정보_수정() throws Exception {
        //given
        long userId = 1L;
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("change@email.com", "changename");
        willDoNothing().given(userServiceV1).updateUser(anyLong(), any(UserUpdateRequest.class));

        //when&then
        mockMvc.perform(patch("/api/v1/users")
                        .header("X-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andDo(document("user-update-info",
                        requestFields(
                                fieldWithPath("email").description("변경할 이메일"),
                                fieldWithPath("nickname").description("변경할 사용자 이름")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 비밀번호_변경() throws Exception {
        //given
        long userId = 1L;
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
                "Password1234!", "Password12345!");
        willDoNothing().given(userServiceV1).updateUserPassword(anyLong(), any(UserUpdatePasswordRequest.class));

        //when&then
        mockMvc.perform(put("/api/v1/users/password")
                        .header("X-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("user-update-password",
                        requestFields(
                                fieldWithPath("oldPassword").description("현재 비밀번호"),
                                fieldWithPath("newPassword").description("변경할 새 비밀번호")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 회원_탈퇴() throws Exception {
        //given
        long userId = 1L;
        UserDeleteRequest request = new UserDeleteRequest("Password1234!");
        willDoNothing().given(userServiceV1).deleteUser(anyLong(), any(UserDeleteRequest.class));

        //when&then
        mockMvc.perform(delete("/api/v1/users")
                        .header("X-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(document("user-delete",
                        requestFields(
                                fieldWithPath("password").description("회원 탈퇴를 위한 비밀번호")
                        )
                ));
    }
}

package com.couponmoa.backend.couponmoauser.domain.user.controller.v1;

import com.couponmoa.backend.couponmoauser.common.service.RedisService;
import com.couponmoa.backend.couponmoauser.config.JwtUtil;
import com.couponmoa.backend.couponmoauser.config.SecurityConfig;
import com.couponmoa.backend.couponmoauser.domain.user.dto.request.SigninRequest;
import com.couponmoa.backend.couponmoauser.domain.user.dto.request.SignupRequest;
import com.couponmoa.backend.couponmoauser.domain.user.dto.response.TokenResponse;
import com.couponmoa.backend.couponmoauser.domain.user.service.v1.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs
@Import({SecurityConfig.class, JwtUtil.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private AuthService authService;

    @Test
    void 회원가입() throws Exception {
        SignupRequest signupRequest = new SignupRequest(
                "user@example.com", "Password1234!", "name", "ROLE_USER");

        willDoNothing().given(authService).signup(any(SignupRequest.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andDo(document("auth-signup",
                                requestFields(
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("password").description("사용자 비밀번호"),
                                        fieldWithPath("nickname").description("사용자 이름"),
                                        fieldWithPath("userRole").description("사용자 역할 (ex: ROLE_USER)")
                                )
                        )
                );
    }

    @Test
    void 로그인() throws Exception {
        SigninRequest signinRequest = new SigninRequest("user@example.com", "Password1234!");
        TokenResponse tokenResponse = new TokenResponse("accessToken", "refreshToken");
        given(authService.signin(any(SigninRequest.class))).willReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"))
                .andDo(document("auth-signin",
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.accessToken").description("발급된 액세스 토큰"),
                                fieldWithPath("data.refreshToken").description("발급된 리프레시 토큰")
                        )
                ));
    }

    @Test
    void 토큰_재발급() throws Exception {
        String refreshToken = "refreshToken";
        TokenResponse tokenResponse = new TokenResponse("accessToken", "refreshToken");
        given(authService.refreshToken(anyString())).willReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .header("Authorization", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"))
                .andDo(document("auth-refresh",
                        requestHeaders(
                                headerWithName("Authorization").description("리프레시 토큰 (헤더)")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.accessToken").description("발급된 액세스 토큰"),
                                fieldWithPath("data.refreshToken").description("발급된 리프레시 토큰")
                        )
                ));
    }

}

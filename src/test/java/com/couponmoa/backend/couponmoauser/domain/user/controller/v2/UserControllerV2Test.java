package com.couponmoa.backend.couponmoauser.domain.user.controller.v2;

import com.couponmoa.backend.couponmoauser.common.service.RedisService;
import com.couponmoa.backend.couponmoauser.config.JwtUtil;
import com.couponmoa.backend.couponmoauser.config.SecurityConfig;
import com.couponmoa.backend.couponmoauser.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.couponmoauser.domain.user.entity.User;
import com.couponmoa.backend.couponmoauser.domain.user.enums.UserRole;
import com.couponmoa.backend.couponmoauser.domain.user.service.v2.UserServiceV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerV2.class)
@Import({SecurityConfig.class, JwtUtil.class})
public class UserControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private UserServiceV2 userServiceV2;

    @Test
    @WithMockUser
    void 사용자_조회() throws Exception {
        //given
        long userId = 1L;
        String email = "email@email.com";
        User user = new User(email, "password", "name", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "id", userId);
        UserResponse mockResponse = UserResponse.fromEntityV2(user);
        given(userServiceV2.findUser(anyLong())).willReturn(mockResponse);

        //when&then
        mockMvc.perform(get("/api/v2/users")
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.imageUrl").value(startsWith("https://d3v4s62h114hel.cloudfront.net/")));
    }
}

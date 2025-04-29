package com.couponmoa.backend.couponmoauser.domain.user.controller.v1;

import com.amazonaws.services.s3.AmazonS3;
import com.couponmoa.backend.couponmoauser.common.service.RedisService;
import com.couponmoa.backend.couponmoauser.config.JwtUtil;
import com.couponmoa.backend.couponmoauser.config.SecurityConfig;
import com.couponmoa.backend.couponmoauser.domain.user.service.v1.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@AutoConfigureRestDocs
@Import({SecurityConfig.class, JwtUtil.class})
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private AmazonS3 amazonS3;

    @MockitoBean
    private UserProfileService userProfileService;

    @Test
    @WithMockUser
    void 프로필_사진_등록() throws Exception {
        Long userId = 1L;

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "profile.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        willDoNothing().given(userProfileService).updateUserImage(anyLong(), any(MultipartFile.class));

        mockMvc.perform(multipart("/api/v1/users/image")
                        .file(file)
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andDo(document("userprofile-upload",
                        requestParts(
                                partWithName("image").description("업로드할 프로필 이미지 파일")
                        )
                ));
    }

    @Test
    @WithMockUser
    void 프로필_사진_삭제() throws Exception {
        Long userId = 1L;

        willDoNothing().given(userProfileService).deleteUserImage(anyLong());

        mockMvc.perform(delete("/api/v1/users/image")
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andDo(document("userprofile-delete"));
    }
}

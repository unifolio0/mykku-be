package com.example.mykku.docs

import com.example.mykku.auth.AuthController
import com.example.mykku.auth.AuthService
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.auth.dto.MobileLoginRequest
import com.example.mykku.member.domain.SocialProvider
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
class AuthControllerRestDocsTest {

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Mock
    private lateinit var authService: AuthService

    @InjectMocks
    private lateinit var authController: AuthController

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .apply<StandaloneMockMvcBuilder>(
                documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(
                        modifyUris()
                            .scheme("https")
                            .host("api.mykku.com")
                            .removePort(),
                        prettyPrint()
                    )
                    .withResponseDefaults(
                        modifyHeaders()
                            .remove("X-Content-Type-Options")
                            .remove("X-XSS-Protection")
                            .remove("Cache-Control")
                            .remove("Pragma")
                            .remove("Expires")
                            .remove("X-Frame-Options"),
                        prettyPrint()
                    )
            )
            .build()
    }

    @Test
    fun `모바일 구글 로그인 API 문서화`() {
        // given
        val request = MobileLoginRequest(
            provider = SocialProvider.GOOGLE,
            accessToken = "google_access_token_example"
        )

        val loginResponse = LoginResponse(
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            tokenType = "Bearer",
            expiresIn = 86400000,
            member = MemberInfo(
                id = "google_123456789",
                email = "user@gmail.com",
                nickname = "홍길동",
                profileImage = "https://lh3.googleusercontent.com/profile.jpg"
            )
        )

        `when`(authService.handleMobileLogin(request)).thenReturn(loginResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/auth/mobile/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andDo(
                document(
                    "auth-mobile-login-google",
                    requestFields(
                        fieldWithPath("provider").type(JsonFieldType.STRING)
                            .description("OAuth 제공자 (GOOGLE, KAKAO, APPLE)"),
                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("OAuth 제공자에서 받은 액세스 토큰"),
                        fieldWithPath("idToken").type(JsonFieldType.STRING).description("Apple 로그인 시 필요한 ID 토큰 (선택사항)")
                            .optional()
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("로그인 응답 데이터"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("JWT 액세스 토큰"),
                        fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 타입"),
                        fieldWithPath("data.expiresIn").type(JsonFieldType.NUMBER).description("토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.member").type(JsonFieldType.OBJECT).description("회원 정보"),
                        fieldWithPath("data.member.id").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.member.email").type(JsonFieldType.STRING).description("회원 이메일"),
                        fieldWithPath("data.member.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("data.member.profileImage").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                            .optional()
                    )
                )
            )
    }

    @Test
    fun `모바일 카카오 로그인 API 문서화`() {
        // given
        val request = MobileLoginRequest(
            provider = SocialProvider.KAKAO,
            accessToken = "kakao_access_token_example"
        )

        val loginResponse = LoginResponse(
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            tokenType = "Bearer",
            expiresIn = 86400000,
            member = MemberInfo(
                id = "kakao_987654321",
                email = "user@kakao.com",
                nickname = "카카오사용자",
                profileImage = "http://k.kakaocdn.net/profile.jpg"
            )
        )

        `when`(authService.handleMobileLogin(request)).thenReturn(loginResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/auth/mobile/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andDo(
                document("auth-mobile-login-kakao")
            )
    }

    @Test
    fun `모바일 애플 로그인 API 문서화`() {
        // given
        val request = MobileLoginRequest(
            provider = SocialProvider.APPLE,
            accessToken = "apple_access_token_example",
            idToken = "apple_id_token_example"
        )

        val loginResponse = LoginResponse(
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            tokenType = "Bearer",
            expiresIn = 86400000,
            member = MemberInfo(
                id = "apple_000123.abc456def789",
                email = "user@privaterelay.appleid.com",
                nickname = "애플사용자",
                profileImage = ""
            )
        )

        `when`(authService.handleMobileLogin(request)).thenReturn(loginResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/auth/mobile/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andDo(
                document("auth-mobile-login-apple")
            )
    }
}

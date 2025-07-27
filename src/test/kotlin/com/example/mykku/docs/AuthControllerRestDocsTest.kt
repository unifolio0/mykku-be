package com.example.mykku.docs

import com.example.mykku.auth.controller.AuthController
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.auth.service.AuthService
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
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
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
    fun `구글 로그인 API 문서화`() {
        // given
        val googleAuthUrl =
            "https://accounts.google.com/o/oauth2/v2/auth?client_id=test&response_type=code&scope=openid%20email%20profile&redirect_uri=http://localhost:8080/api/v1/auth/google/callback&access_type=offline"

        `when`(authService.getGoogleAuthUrl()).thenReturn(googleAuthUrl)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/auth/google/login")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", googleAuthUrl))
            .andDo(
                document("auth-google-login")
            )
    }

    @Test
    fun `구글 로그인 콜백 API 문서화`() {
        // given
        val code = "google_auth_code_example"
        val loginResponse = LoginResponse(
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            tokenType = "Bearer",
            expiresIn = 86400000,
            member = MemberInfo(
                id = 1L,
                email = "user@example.com",
                nickname = "홍길동",
                profileImage = "https://example.com/profile.jpg"
            )
        )

        `when`(authService.handleGoogleCallback(code)).thenReturn(loginResponse)

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/auth/google/callback")
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andDo(
                document(
                    "auth-google-callback",
                    queryParameters(
                        parameterWithName("code").description("구글에서 제공받은 인증 코드")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("로그인 응답 데이터"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("JWT 액세스 토큰"),
                        fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 타입"),
                        fieldWithPath("data.expiresIn").type(JsonFieldType.NUMBER).description("토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.member").type(JsonFieldType.OBJECT).description("회원 정보"),
                        fieldWithPath("data.member.id").type(JsonFieldType.NUMBER).description("회원 ID"),
                        fieldWithPath("data.member.email").type(JsonFieldType.STRING).description("회원 이메일"),
                        fieldWithPath("data.member.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("data.member.profileImage").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                            .optional()
                    )
                )
            )
    }
}

package com.example.mykku.docs

import com.example.mykku.auth.controller.AuthController
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.auth.service.AuthService
import com.example.mykku.member.domain.Member
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
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
class AppleAuthControllerRestDocsTest {

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
        val messageConverter = MappingJackson2HttpMessageConverter(objectMapper)
        
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(authController)
            .setMessageConverters(messageConverter)
            .apply<StandaloneMockMvcBuilder>(documentationConfiguration(restDocumentation))
            .alwaysDo<StandaloneMockMvcBuilder>(
                document(
                    "{method-name}",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                )
            )
            .build()
    }

    @Test
    fun `애플 로그인 페이지 리다이렉트`() {
        val appleAuthUrl = "https://appleid.apple.com/auth/authorize?client_id=test-client-id&redirect_uri=http://localhost:8080/api/v1/auth/apple/callback&response_type=code&state=test-state&scope=name%20email&response_mode=form_post"
        val state = "test-state"
        
        `when`(authService.getAppleAuthUrl(state)).thenReturn(appleAuthUrl)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/auth/apple/login")
                .param("state", state)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(appleAuthUrl))
            .andDo(
                document(
                    "auth-apple-login",
                    queryParameters(
                        parameterWithName("state").description("Apple OAuth state 파라미터 (선택사항)")
                    ),
                    responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("Apple OAuth 인증 페이지 URL")
                    )
                )
            )
    }

    @Test
    fun `애플 로그인 콜백 처리`() {
        val authCode = "test-auth-code"
        val state = "test-state"
        val member = Member(
            id = "apple_000123.abc456def",
            nickname = "애플사용자",
            role = "USER",
            profileImage = "",
            provider = SocialProvider.APPLE,
            socialId = "000123.abc456def",
            email = "user@privaterelay.appleid.com"
        )
        val loginResponse = LoginResponse(
            accessToken = "test-jwt-token",
            expiresIn = 86400000L,
            member = MemberInfo(
                id = member.id,
                nickname = member.nickname,
                profileImage = member.profileImage,
                email = member.email
            )
        )

        `when`(authService.handleAppleCallback(authCode)).thenReturn(loginResponse)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/auth/apple/callback")
                .param("code", authCode)
                .param("state", state)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(jsonPath("$.data.accessToken").value(loginResponse.accessToken))
            .andExpect(jsonPath("$.data.member.id").value(loginResponse.member.id))
            .andExpect(jsonPath("$.data.member.nickname").value(loginResponse.member.nickname))
            .andExpect(jsonPath("$.data.member.profileImage").value(loginResponse.member.profileImage))
            .andExpect(jsonPath("$.data.member.email").value(loginResponse.member.email))
            .andExpect(jsonPath("$.data.expiresIn").value(loginResponse.expiresIn))
            .andDo(
                document(
                    "auth-apple-callback",
                    org.springframework.restdocs.request.RequestDocumentation.formParameters(
                        parameterWithName("code").description("Apple OAuth 인증 코드"),
                        parameterWithName("state").description("Apple OAuth state 파라미터 (선택사항)")
                    ),
                    responseFields(
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.accessToken").description("JWT 액세스 토큰"),
                        fieldWithPath("data.tokenType").description("토큰 타입"),
                        fieldWithPath("data.expiresIn").description("토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.member.id").description("회원 ID"),
                        fieldWithPath("data.member.nickname").description("회원 닉네임"),
                        fieldWithPath("data.member.profileImage").description("프로필 이미지 URL"),
                        fieldWithPath("data.member.email").description("이메일 주소")
                    )
                )
            )
    }
}
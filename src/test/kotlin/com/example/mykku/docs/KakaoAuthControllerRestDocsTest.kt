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
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class, RestDocumentationExtension::class)
class KakaoAuthControllerRestDocsTest {

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
    fun `카카오 로그인 페이지 리다이렉트`() {
        val kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?client_id=test-client-id&response_type=code&redirect_uri=http://localhost:8080/api/v1/auth/kakao/callback"
        
        `when`(authService.getKakaoAuthUrl()).thenReturn(kakaoAuthUrl)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/auth/kakao/login")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(kakaoAuthUrl))
            .andDo(
                document(
                    "auth-kakao-login",
                    responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("카카오 OAuth 인증 페이지 URL")
                    )
                )
            )
    }

    @Test
    fun `카카오 로그인 콜백 처리`() {
        val authCode = "test-auth-code"
        val member = Member(
            id = "kakao_123456789",
            nickname = "카카오사용자",
            role = "USER",
            profileImage = "https://k.kakaocdn.net/profile.jpg",
            provider = SocialProvider.KAKAO,
            socialId = "123456789",
            email = "user@kakao.com"
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

        `when`(authService.handleKakaoCallback(authCode)).thenReturn(loginResponse)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/auth/kakao/callback")
                .param("code", authCode)
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
                    "auth-kakao-callback",
                    queryParameters(
                        parameterWithName("code").description("카카오 OAuth 인증 코드")
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
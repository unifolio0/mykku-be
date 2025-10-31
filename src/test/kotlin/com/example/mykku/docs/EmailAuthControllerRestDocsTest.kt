package com.example.mykku.docs

import com.example.mykku.BaseControllerRestDocsTest
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.email.EmailAuthController
import com.example.mykku.email.EmailAuthService
import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.dto.*
import com.example.mykku.email.exception.EmailAuthExceptionHandler
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

class EmailAuthControllerRestDocsTest : BaseControllerRestDocsTest() {

    @Mock
    private lateinit var emailAuthService: EmailAuthService

    @InjectMocks
    private lateinit var emailAuthController: EmailAuthController

    override fun createMockMvcBuilder(): StandaloneMockMvcBuilder {
        return MockMvcBuilders.standaloneSetup(emailAuthController)
    }

    override fun getControllerAdvice(): Any {
        return EmailAuthExceptionHandler()
    }

    @Test
    fun `인증 코드 발송 API 문서화`() {
        val request = SendVerificationCodeRequest(
            email = "user@example.com",
            purpose = VerificationPurpose.SIGNUP
        )

        doNothing().`when`(emailAuthService).sendVerificationCode(request.email, request.purpose)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/email-auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("인증 코드가 발송되었습니다"))
            .andDo(
                document(
                    "email-auth-send-code",
                    requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("인증 코드를 받을 이메일 주소"),
                        fieldWithPath("purpose").type(JsonFieldType.STRING)
                            .description("인증 목적 (SIGNUP: 회원가입, PASSWORD_RESET: 비밀번호 재설정)")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터 (없음)").optional()
                    )
                )
            )
    }

    @Test
    fun `인증 코드 검증 API 문서화`() {
        val request = VerifyCodeRequest(
            email = "user@example.com",
            code = "123456",
            purpose = VerificationPurpose.SIGNUP
        )

        doNothing().`when`(emailAuthService).verifyCode(request.email, request.code, request.purpose)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/email-auth/verify-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("인증이 완료되었습니다"))
            .andDo(
                document(
                    "email-auth-verify-code",
                    requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("인증할 이메일 주소"),
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("발송받은 6자리 인증 코드"),
                        fieldWithPath("purpose").type(JsonFieldType.STRING)
                            .description("인증 목적 (SIGNUP: 회원가입, PASSWORD_RESET: 비밀번호 재설정)")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터 (없음)").optional()
                    )
                )
            )
    }

    @Test
    fun `회원가입 API 문서화`() {
        val request = SignupRequest(
            email = "newuser@example.com",
            password = "password123!",
            nickname = "신규유저"
        )

        val loginResponse = LoginResponse(
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh...",
            accessTokenExpiresIn = 86400000,
            refreshTokenExpiresIn = 1209600000,
            member = MemberInfo(
                id = "newUserId",
                email = request.email,
                nickname = request.nickname,
                profileImage = ""
            ),
            isExistingUser = false
        )

        `when`(emailAuthService.signup(request.email, request.password, request.nickname))
            .thenReturn(loginResponse)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/email-auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andDo(
                document(
                    "email-auth-signup",
                    requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("가입할 이메일 주소"),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호 (최소 8자, 영문/숫자/특수문자 포함)"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                            .description("닉네임 (최대 10자, 한글/영문/숫자/공백 가능)")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("로그인 응답 데이터"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("JWT 액세스 토큰"),
                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("JWT 리프레시 토큰"),
                        fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 타입"),
                        fieldWithPath("data.accessTokenExpiresIn").type(JsonFieldType.NUMBER).description("액세스 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.refreshTokenExpiresIn").type(JsonFieldType.NUMBER).description("리프레시 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.member").type(JsonFieldType.OBJECT).description("회원 정보"),
                        fieldWithPath("data.member.id").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.member.email").type(JsonFieldType.STRING).description("회원 이메일"),
                        fieldWithPath("data.member.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("data.member.profileImage").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                        fieldWithPath("data.isExistingUser").type(JsonFieldType.BOOLEAN).description("기존 가입자 여부")
                    )
                )
            )
    }

    @Test
    fun `이메일 로그인 API 문서화`() {
        val request = EmailLoginRequest(
            email = "user@example.com",
            password = "password123!"
        )

        val loginResponse = LoginResponse(
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh...",
            accessTokenExpiresIn = 86400000,
            refreshTokenExpiresIn = 1209600000,
            member = MemberInfo(
                id = "userId",
                email = request.email,
                nickname = "테스트유저",
                profileImage = ""
            ),
            isExistingUser = true
        )

        `when`(emailAuthService.login(request.email, request.password))
            .thenReturn(loginResponse)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/email-auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andDo(
                document(
                    "email-auth-login",
                    requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("이메일 주소"),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("비밀번호")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("로그인 응답 데이터"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("JWT 액세스 토큰"),
                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("JWT 리프레시 토큰"),
                        fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 타입"),
                        fieldWithPath("data.accessTokenExpiresIn").type(JsonFieldType.NUMBER).description("액세스 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.refreshTokenExpiresIn").type(JsonFieldType.NUMBER).description("리프레시 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.member").type(JsonFieldType.OBJECT).description("회원 정보"),
                        fieldWithPath("data.member.id").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.member.email").type(JsonFieldType.STRING).description("회원 이메일"),
                        fieldWithPath("data.member.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("data.member.profileImage").type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                        fieldWithPath("data.isExistingUser").type(JsonFieldType.BOOLEAN).description("기존 가입자 여부")
                    )
                )
            )
    }

    @Test
    fun `비밀번호 재설정 API 문서화`() {
        val request = ResetPasswordRequest(
            email = "user@example.com",
            code = "123456",
            newPassword = "newPassword123!"
        )

        doNothing().`when`(emailAuthService).resetPassword(request.email, request.code, request.newPassword)

        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/email-auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("비밀번호가 재설정되었습니다"))
            .andDo(
                document(
                    "email-auth-reset-password",
                    requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("비밀번호를 재설정할 이메일 주소"),
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("발송받은 6자리 인증 코드"),
                        fieldWithPath("newPassword").type(JsonFieldType.STRING)
                            .description("새로운 비밀번호 (최소 8자, 영문/숫자/특수문자 포함)")
                    ),
                    responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터 (없음)").optional()
                    )
                )
            )
    }
}

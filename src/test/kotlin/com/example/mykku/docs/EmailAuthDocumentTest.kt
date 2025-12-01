package com.example.mykku.docs

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.dto.*
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

class EmailAuthDocumentTest : BaseDocumentTest() {

    @Test
    fun `인증 코드 발송`() {
        val request = SendVerificationCodeRequest(
            email = "user@example.com",
            purpose = VerificationPurpose.SIGNUP
        )

        doNothing().`when`(emailAuthService).sendVerificationCode(any(), any())

        val documentFilter = document("email-auth/send-code", 200)
            .request(
                request()
                    .tag(Tag.EMAIL_AUTH_API)
                    .summary("인증 코드 발송")
                    .description("이메일로 인증 코드를 발송합니다.")
                    .requestBodyField(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("인증 코드를 받을 이메일 주소"),
                        fieldWithPath("purpose").type(JsonFieldType.STRING)
                            .description("인증 목적 (SIGNUP: 회원가입, PASSWORD_RESET: 비밀번호 재설정)")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/email-auth/send-code")
            .then()
            .statusCode(200)
    }

    @Test
    fun `인증 코드 검증`() {
        val request = VerifyCodeRequest(
            email = "user@example.com",
            code = "123456",
            purpose = VerificationPurpose.SIGNUP
        )

        doNothing().`when`(emailAuthService).verifyCode(any(), any(), any())

        val documentFilter = document("email-auth/verify-code", 200)
            .request(
                request()
                    .tag(Tag.EMAIL_AUTH_API)
                    .summary("인증 코드 검증")
                    .description("발송된 인증 코드를 검증합니다.")
                    .requestBodyField(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("인증할 이메일 주소"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("발송받은 6자리 인증 코드"),
                        fieldWithPath("purpose").type(JsonFieldType.STRING)
                            .description("인증 목적 (SIGNUP: 회원가입, PASSWORD_RESET: 비밀번호 재설정)")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/email-auth/verify-code")
            .then()
            .statusCode(200)
    }

    @Test
    fun `이메일 회원가입`() {
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

        `when`(emailAuthService.signup(any(), any(), any())).thenReturn(loginResponse)

        val documentFilter = document("email-auth/signup", 200)
            .request(
                request()
                    .tag(Tag.EMAIL_AUTH_API)
                    .summary("이메일 회원가입")
                    .description("이메일로 회원가입합니다.")
                    .requestBodyField(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("가입할 이메일 주소"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호 (최소 8자, 영문/숫자/특수문자 포함)"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임 (최대 10자)")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("로그인 응답 데이터"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("JWT 액세스 토큰"),
                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("JWT 리프레시 토큰"),
                        fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 타입"),
                        fieldWithPath("data.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                            .description("액세스 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.refreshTokenExpiresIn").type(JsonFieldType.NUMBER)
                            .description("리프레시 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.member").type(JsonFieldType.OBJECT).description("회원 정보"),
                        fieldWithPath("data.member.id").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.member.email").type(JsonFieldType.STRING).description("회원 이메일"),
                        fieldWithPath("data.member.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("data.member.profileImage").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                            .optional(),
                        fieldWithPath("data.isExistingUser").type(JsonFieldType.BOOLEAN).description("기존 가입자 여부")
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/email-auth/signup")
            .then()
            .statusCode(200)
    }

    @Test
    fun `이메일 로그인`() {
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

        `when`(emailAuthService.login(any(), any())).thenReturn(loginResponse)

        val documentFilter = document("email-auth/login", 200)
            .request(
                request()
                    .tag(Tag.EMAIL_AUTH_API)
                    .summary("이메일 로그인")
                    .description("이메일로 로그인합니다.")
                    .requestBodyField(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일 주소"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("로그인 응답 데이터"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("JWT 액세스 토큰"),
                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("JWT 리프레시 토큰"),
                        fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 타입"),
                        fieldWithPath("data.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                            .description("액세스 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.refreshTokenExpiresIn").type(JsonFieldType.NUMBER)
                            .description("리프레시 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.member").type(JsonFieldType.OBJECT).description("회원 정보"),
                        fieldWithPath("data.member.id").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.member.email").type(JsonFieldType.STRING).description("회원 이메일"),
                        fieldWithPath("data.member.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("data.member.profileImage").type(JsonFieldType.STRING).description("프로필 이미지 URL")
                            .optional(),
                        fieldWithPath("data.isExistingUser").type(JsonFieldType.BOOLEAN).description("기존 가입자 여부")
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/email-auth/login")
            .then()
            .statusCode(200)
    }

    @Test
    fun `비밀번호 재설정`() {
        val request = ResetPasswordRequest(
            email = "user@example.com",
            code = "123456",
            newPassword = "newPassword123!"
        )

        doNothing().`when`(emailAuthService).resetPassword(any(), any(), any())

        val documentFilter = document("email-auth/reset-password", 200)
            .request(
                request()
                    .tag(Tag.EMAIL_AUTH_API)
                    .summary("비밀번호 재설정")
                    .description("인증 코드를 통해 비밀번호를 재설정합니다.")
                    .requestBodyField(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("비밀번호를 재설정할 이메일 주소"),
                        fieldWithPath("code").type(JsonFieldType.STRING).description("발송받은 6자리 인증 코드"),
                        fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새로운 비밀번호 (최소 8자)")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/email-auth/reset-password")
            .then()
            .statusCode(200)
    }

    @Test
    fun `임시 비밀번호 발송`() {
        val request = SendTemporaryPasswordRequest(email = "user@example.com")

        doNothing().`when`(emailAuthService).sendTemporaryPassword(any())

        val documentFilter = document("email-auth/send-temporary-password", 200)
            .request(
                request()
                    .tag(Tag.EMAIL_AUTH_API)
                    .summary("임시 비밀번호 발송")
                    .description("임시 비밀번호를 이메일로 발송합니다.")
                    .requestBodyField(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("임시 비밀번호를 받을 이메일 주소")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터").optional()
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/email-auth/send-temporary-password")
            .then()
            .statusCode(200)
    }
}

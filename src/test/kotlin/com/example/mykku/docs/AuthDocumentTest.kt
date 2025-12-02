package com.example.mykku.docs

import com.example.mykku.auth.AuthService
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.auth.dto.MobileLoginRequest
import com.example.mykku.auth.dto.RefreshTokenRequest
import com.example.mykku.auth.dto.RefreshTokenResponse
import com.example.mykku.auth.exception.AuthErrorCode
import com.example.mykku.auth.exception.AuthException
import com.example.mykku.member.domain.SocialProvider
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.context.bean.override.mockito.MockitoBean

class AuthDocumentTest : BaseDocumentTest() {

    @Test
    fun `모바일 구글 로그인`() {
        val request = MobileLoginRequest(
            provider = SocialProvider.GOOGLE,
            accessToken = "google_access_token_example"
        )

        val loginResponse = LoginResponse(
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh...",
            tokenType = "Bearer",
            accessTokenExpiresIn = 86400000,
            refreshTokenExpiresIn = 1209600000,
            member = MemberInfo(
                id = "google_123456789",
                email = "user@gmail.com",
                nickname = "홍길동",
                profileImage = "https://lh3.googleusercontent.com/profile.jpg"
            ),
            isExistingUser = true
        )

        `when`(authService.handleMobileLogin(request)).thenReturn(loginResponse)

        val documentFilter = document("auth/mobile-login", 200)
            .request(
                request()
                    .tag(Tag.AUTH_API)
                    .summary("모바일 소셜 로그인")
                    .description("모바일 앱에서 OAuth 제공자(Google, Kakao, Apple, Naver)를 통해 로그인합니다.")
                    .requestBodyField(
                        fieldWithPath("provider").type(JsonFieldType.STRING)
                            .description("OAuth 제공자 (GOOGLE, KAKAO, APPLE, NAVER)"),
                        fieldWithPath("accessToken").type(JsonFieldType.STRING)
                            .description("OAuth 제공자에서 받은 액세스 토큰"),
                        fieldWithPath("idToken").type(JsonFieldType.STRING)
                            .description("Apple 로그인 시 필요한 ID 토큰 (선택사항)")
                            .optional()
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("로그인 응답 데이터"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("JWT 액세스 토큰"),
                        fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 타입"),
                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("JWT 리프레시 토큰"),
                        fieldWithPath("data.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                            .description("액세스 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.refreshTokenExpiresIn").type(JsonFieldType.NUMBER)
                            .description("리프레시 토큰 만료 시간 (밀리초)"),
                        fieldWithPath("data.member").type(JsonFieldType.OBJECT).description("회원 정보"),
                        fieldWithPath("data.member.id").type(JsonFieldType.STRING).description("회원 ID"),
                        fieldWithPath("data.member.email").type(JsonFieldType.STRING).description("회원 이메일"),
                        fieldWithPath("data.member.nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                        fieldWithPath("data.member.profileImage").type(JsonFieldType.STRING)
                            .description("프로필 이미지 URL").optional(),
                        fieldWithPath("data.isExistingUser").type(JsonFieldType.BOOLEAN)
                            .description("기존 가입자 여부 (true: 기존 가입자, false: 신규 가입자)")
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/auth/mobile/login")
            .then()
            .statusCode(200)
    }

    @Test
    fun `모바일 소셜 로그인 - 유효하지 않은 토큰`() {
        val request = MobileLoginRequest(
            provider = SocialProvider.GOOGLE,
            accessToken = "invalid_access_token"
        )

        `when`(authService.handleMobileLogin(request))
            .thenThrow(AuthException(AuthErrorCode.OAUTH_INVALID_TOKEN))

        val documentFilter = document("auth/mobile-login", "OAUTH_INVALID_TOKEN")
            .request(
                request()
                    .tag(Tag.AUTH_API)
                    .summary("모바일 소셜 로그인 - 유효하지 않은 토큰")
                    .description("OAuth 제공자로부터 받은 토큰이 유효하지 않을 때 발생하는 에러입니다.")
                    .requestBodyField(
                        fieldWithPath("provider").type(JsonFieldType.STRING)
                            .description("OAuth 제공자"),
                        fieldWithPath("accessToken").type(JsonFieldType.STRING)
                            .description("OAuth 제공자에서 받은 액세스 토큰"),
                        fieldWithPath("idToken").type(JsonFieldType.STRING)
                            .description("Apple 로그인 시 필요한 ID 토큰")
                            .optional()
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/auth/mobile/login")
            .then()
            .statusCode(401)
    }

    @Test
    fun `모바일 소셜 로그인 - 사용자 정보 가져오기 실패`() {
        val request = MobileLoginRequest(
            provider = SocialProvider.GOOGLE,
            accessToken = "google_access_token_example"
        )

        `when`(authService.handleMobileLogin(request))
            .thenThrow(AuthException(AuthErrorCode.OAUTH_USER_INFO_FAILED))

        val documentFilter = document("auth/mobile-login", "OAUTH_USER_INFO_FAILED")
            .request(
                request()
                    .tag(Tag.AUTH_API)
                    .summary("모바일 소셜 로그인 - 사용자 정보 가져오기 실패")
                    .description("OAuth 제공자로부터 사용자 정보를 가져오는데 실패했을 때 발생하는 에러입니다.")
                    .requestBodyField(
                        fieldWithPath("provider").type(JsonFieldType.STRING)
                            .description("OAuth 제공자"),
                        fieldWithPath("accessToken").type(JsonFieldType.STRING)
                            .description("OAuth 제공자에서 받은 액세스 토큰"),
                        fieldWithPath("idToken").type(JsonFieldType.STRING)
                            .description("Apple 로그인 시 필요한 ID 토큰")
                            .optional()
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/auth/mobile/login")
            .then()
            .statusCode(400)
    }

    @Test
    fun `모바일 소셜 로그인 - 외부 서비스 오류`() {
        val request = MobileLoginRequest(
            provider = SocialProvider.GOOGLE,
            accessToken = "google_access_token_example"
        )

        `when`(authService.handleMobileLogin(request))
            .thenThrow(AuthException(AuthErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR))

        val documentFilter = document("auth/mobile-login", "OAUTH_EXTERNAL_SERVICE_ERROR")
            .request(
                request()
                    .tag(Tag.AUTH_API)
                    .summary("모바일 소셜 로그인 - 외부 서비스 오류")
                    .description("OAuth 제공자의 외부 서비스에서 오류가 발생했을 때 나타나는 에러입니다.")
                    .requestBodyField(
                        fieldWithPath("provider").type(JsonFieldType.STRING)
                            .description("OAuth 제공자"),
                        fieldWithPath("accessToken").type(JsonFieldType.STRING)
                            .description("OAuth 제공자에서 받은 액세스 토큰"),
                        fieldWithPath("idToken").type(JsonFieldType.STRING)
                            .description("Apple 로그인 시 필요한 ID 토큰")
                            .optional()
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/auth/mobile/login")
            .then()
            .statusCode(503)
    }

    @Test
    fun `토큰 갱신`() {
        val request = RefreshTokenRequest(
            refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh.token.example"
        )

        val response = RefreshTokenResponse(
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.new.access.token",
            tokenType = "Bearer",
            expiresIn = 86400000
        )

        `when`(authService.refreshAccessToken(request)).thenReturn(response)

        val documentFilter = document("auth/refresh", 200)
            .request(
                request()
                    .tag(Tag.AUTH_API)
                    .summary("액세스 토큰 갱신")
                    .description("리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
                    .requestBodyField(
                        fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                            .description("리프레시 토큰")
                    )
            )
            .response(
                response()
                    .responseBodyField(
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("토큰 갱신 응답 데이터"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                            .description("새로운 JWT 액세스 토큰"),
                        fieldWithPath("data.tokenType").type(JsonFieldType.STRING).description("토큰 타입"),
                        fieldWithPath("data.expiresIn").type(JsonFieldType.NUMBER)
                            .description("액세스 토큰 만료 시간 (밀리초)")
                    )
            )
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/auth/refresh")
            .then()
            .statusCode(200)
    }

    @Test
    fun `토큰 갱신 - 유효하지 않은 리프레시 토큰`() {
        val request = RefreshTokenRequest(
            refreshToken = "invalid_refresh_token"
        )

        `when`(authService.refreshAccessToken(request))
            .thenThrow(AuthException(AuthErrorCode.INVALID_TOKEN))

        val documentFilter = document("auth/refresh", "INVALID_TOKEN")
            .request(
                request()
                    .tag(Tag.AUTH_API)
                    .summary("토큰 갱신 - 유효하지 않은 리프레시 토큰")
                    .description("유효하지 않거나 만료된 리프레시 토큰으로 갱신을 시도할 때 발생하는 에러입니다.")
                    .requestBodyField(
                        fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                            .description("리프레시 토큰")
                    )
            )
            .response(RestDocumentationResponse.ERROR_RESPONSE)
            .build()

        given(documentFilter)
            .contentType(ContentType.JSON)
            .body(objectMapper.writeValueAsString(request))
            .`when`()
            .post("/api/v1/auth/refresh")
            .then()
            .statusCode(401)
    }
}

package com.example.mykku.auth

import com.example.mykku.BaseServiceTest
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.auth.dto.MobileLoginRequest
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.auth.tool.OAuthLoginStrategy
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.tool.MemberReader
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.whenever

class AuthServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var memberReader: MemberReader

    @Mock
    private lateinit var googleLoginStrategy: OAuthLoginStrategy

    @Mock
    private lateinit var kakaoLoginStrategy: OAuthLoginStrategy

    @Mock
    private lateinit var appleLoginStrategy: OAuthLoginStrategy

    @Mock
    private lateinit var naverLoginStrategy: OAuthLoginStrategy

    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = AuthService(
            jwtTokenProvider,
            memberReader,
            listOf(googleLoginStrategy, kakaoLoginStrategy, appleLoginStrategy, naverLoginStrategy)
        )
    }

    @Test
    fun `handleMobileLogin - GOOGLE 로그인을 처리한다`() {
        val request = MobileLoginRequest(provider = SocialProvider.GOOGLE, accessToken = "google_token", idToken = null)
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            refreshToken = "refresh_token",
            tokenType = "Bearer",
            accessTokenExpiresIn = 86400000L,
            refreshTokenExpiresIn = 1209600000L,
            member = MemberInfo(
                id = "google_123456",
                email = "test@google.com",
                nickname = "Test User",
                profileImage = "profile.jpg"
            ),
            isExistingUser = true
        )

        whenever(googleLoginStrategy.supports(SocialProvider.GOOGLE)).thenReturn(true)
        whenever(googleLoginStrategy.login("google_token")).thenReturn(loginResponse)

        val result = authService.handleMobileLogin(request)

        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleMobileLogin - KAKAO 로그인을 처리한다`() {
        val request = MobileLoginRequest(provider = SocialProvider.KAKAO, accessToken = "kakao_token", idToken = null)
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            refreshToken = "refresh_token",
            tokenType = "Bearer",
            accessTokenExpiresIn = 86400000L,
            refreshTokenExpiresIn = 1209600000L,
            member = MemberInfo(
                id = "kakao_123456",
                email = "test@kakao.com",
                nickname = "카카오유저",
                profileImage = "kakao_profile.jpg"
            ),
            isExistingUser = true
        )

        whenever(kakaoLoginStrategy.supports(SocialProvider.KAKAO)).thenReturn(true)
        whenever(kakaoLoginStrategy.login("kakao_token")).thenReturn(loginResponse)

        val result = authService.handleMobileLogin(request)

        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleMobileLogin - APPLE 로그인을 처리한다`() {
        val request = MobileLoginRequest(provider = SocialProvider.APPLE, accessToken = null, idToken = "apple_token")
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            refreshToken = "refresh_token",
            tokenType = "Bearer",
            accessTokenExpiresIn = 86400000L,
            refreshTokenExpiresIn = 1209600000L,
            member = MemberInfo(
                id = "apple_apple.user.123456",
                email = "test@privaterelay.appleid.com",
                nickname = "애플사용자",
                profileImage = ""
            ),
            isExistingUser = true
        )

        whenever(appleLoginStrategy.supports(SocialProvider.APPLE)).thenReturn(true)
        whenever(appleLoginStrategy.login("apple_token")).thenReturn(loginResponse)

        val result = authService.handleMobileLogin(request)

        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleMobileLogin - NAVER 로그인을 처리한다`() {
        val request = MobileLoginRequest(provider = SocialProvider.NAVER, accessToken = "naver_token", idToken = null)
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            refreshToken = "refresh_token",
            tokenType = "Bearer",
            accessTokenExpiresIn = 86400000L,
            refreshTokenExpiresIn = 1209600000L,
            member = MemberInfo(
                id = "naver_naver123456",
                email = "test@naver.com",
                nickname = "네이버별명",
                profileImage = "naver_profile.jpg"
            ),
            isExistingUser = true
        )

        whenever(naverLoginStrategy.supports(SocialProvider.NAVER)).thenReturn(true)
        whenever(naverLoginStrategy.login("naver_token")).thenReturn(loginResponse)

        val result = authService.handleMobileLogin(request)

        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleGoogleMobileLogin - 새 사용자를 생성한다`() {
        val request = MobileLoginRequest(provider = SocialProvider.GOOGLE, accessToken = "google_token", idToken = null)
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            refreshToken = "refresh_token",
            tokenType = "Bearer",
            accessTokenExpiresIn = 86400000L,
            refreshTokenExpiresIn = 1209600000L,
            member = MemberInfo(
                id = "google_123456",
                email = "new@google.com",
                nickname = "New User",
                profileImage = "profile.jpg"
            ),
            isExistingUser = false
        )

        whenever(googleLoginStrategy.supports(SocialProvider.GOOGLE)).thenReturn(true)
        whenever(googleLoginStrategy.login("google_token")).thenReturn(loginResponse)

        val result = authService.handleMobileLogin(request)

        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleKakaoMobileLogin - 이메일이 없는 경우 기본 이메일을 생성한다`() {
        val request = MobileLoginRequest(provider = SocialProvider.KAKAO, accessToken = "kakao_token", idToken = null)
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            refreshToken = "refresh_token",
            tokenType = "Bearer",
            accessTokenExpiresIn = 86400000L,
            refreshTokenExpiresIn = 1209600000L,
            member = MemberInfo(
                id = "kakao_123456",
                email = "kakao_123456@kakao.com",
                nickname = "카카오유저",
                profileImage = "kakao_profile.jpg"
            ),
            isExistingUser = true
        )

        whenever(kakaoLoginStrategy.supports(SocialProvider.KAKAO)).thenReturn(true)
        whenever(kakaoLoginStrategy.login("kakao_token")).thenReturn(loginResponse)

        val result = authService.handleMobileLogin(request)

        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleAppleMobileLogin - 이메일이 없는 경우 기본 이메일을 생성한다`() {
        val request = MobileLoginRequest(provider = SocialProvider.APPLE, accessToken = null, idToken = "apple_token")
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            refreshToken = "refresh_token",
            tokenType = "Bearer",
            accessTokenExpiresIn = 86400000L,
            refreshTokenExpiresIn = 1209600000L,
            member = MemberInfo(
                id = "apple_apple.user.123456",
                email = "apple_apple.user.123456@privaterelay.appleid.com",
                nickname = "애플사용자",
                profileImage = ""
            ),
            isExistingUser = true
        )

        whenever(appleLoginStrategy.supports(SocialProvider.APPLE)).thenReturn(true)
        whenever(appleLoginStrategy.login("apple_token")).thenReturn(loginResponse)

        val result = authService.handleMobileLogin(request)

        assertEquals(loginResponse.accessToken, result.accessToken)
    }
}

package com.example.mykku.auth

import com.example.mykku.auth.dto.*
import com.example.mykku.auth.tool.AppleOauthClient
import com.example.mykku.auth.tool.GoogleOauthClient
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.auth.tool.KakaoOauthClient
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.MemberWriter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var memberReader: MemberReader

    @Mock
    private lateinit var memberWriter: MemberWriter

    @Mock
    private lateinit var googleOauthClient: GoogleOauthClient

    @Mock
    private lateinit var kakaoOauthClient: KakaoOauthClient

    @Mock
    private lateinit var appleOauthClient: AppleOauthClient

    @InjectMocks
    private lateinit var authService: AuthService

    @Test
    fun `handleMobileLogin - GOOGLE 로그인을 처리한다`() {
        // given
        val request = MobileLoginRequest(provider = SocialProvider.GOOGLE, accessToken = "google_token", idToken = null)
        val userInfo = GoogleUserInfo(
            id = "123456",
            email = "test@google.com",
            name = "Test User",
            givenName = "Test",
            familyName = "User",
            picture = "profile.jpg",
            verifiedEmail = true
        )
        val member = Member(
            id = "google_123456",
            nickname = "Test User",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@google.com"
        )
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            tokenType = "Bearer",
            expiresIn = 3600L,
            member = MemberInfo(
                id = member.id,
                email = member.email,
                nickname = member.nickname,
                profileImage = member.profileImage
            )
        )

        whenever(googleOauthClient.verifyAndGetUserInfo("google_token")).thenReturn(userInfo)
        whenever(memberReader.findById("google_123456")).thenReturn(Optional.of(member))
        whenever(jwtTokenProvider.createLoginResponse(member, userInfo.email)).thenReturn(loginResponse)

        // when
        val result = authService.handleMobileLogin(request)

        // then
        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleMobileLogin - KAKAO 로그인을 처리한다`() {
        // given
        val request = MobileLoginRequest(provider = SocialProvider.KAKAO, accessToken = "kakao_token", idToken = null)
        val userInfo = KakaoUserInfo(
            id = 123456L,
            connectedAt = "2023-01-01T00:00:00Z",
            properties = KakaoUserInfo.Properties(
                nickname = "카카오유저",
                profileImage = "kakao_profile.jpg",
                thumbnailImage = null
            ),
            kakaoAccount = KakaoUserInfo.KakaoAccount(
                profileNicknameNeedsAgreement = false,
                profileImageNeedsAgreement = false,
                profile = KakaoUserInfo.KakaoAccount.Profile(
                    nickname = "카카오유저",
                    thumbnailImageUrl = null,
                    profileImageUrl = "kakao_profile.jpg",
                    isDefaultImage = false
                ),
                hasEmail = true,
                emailNeedsAgreement = false,
                isEmailValid = true,
                isEmailVerified = true,
                email = "test@kakao.com"
            )
        )
        val member = Member(
            id = "kakao_123456",
            nickname = "카카오유저",
            role = "USER",
            profileImage = "kakao_profile.jpg",
            provider = SocialProvider.KAKAO,
            socialId = "123456",
            email = "test@kakao.com"
        )
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            tokenType = "Bearer",
            expiresIn = 3600L,
            member = MemberInfo(
                id = member.id,
                email = member.email,
                nickname = member.nickname,
                profileImage = member.profileImage
            )
        )

        whenever(kakaoOauthClient.verifyAndGetUserInfo("kakao_token")).thenReturn(userInfo)
        whenever(memberReader.findById("kakao_123456")).thenReturn(Optional.of(member))
        whenever(jwtTokenProvider.createLoginResponse(member, "test@kakao.com")).thenReturn(loginResponse)

        // when
        val result = authService.handleMobileLogin(request)

        // then
        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleMobileLogin - APPLE 로그인을 처리한다`() {
        // given
        val request = MobileLoginRequest(provider = SocialProvider.APPLE, accessToken = null, idToken = "apple_token")
        val userInfo = AppleUserInfo(
            sub = "apple.user.123456",
            email = "test@privaterelay.appleid.com"
        )
        val member = Member(
            id = "apple_apple.user.123456",
            nickname = "애플사용자",
            role = "USER",
            profileImage = "",
            provider = SocialProvider.APPLE,
            socialId = "apple.user.123456",
            email = "test@privaterelay.appleid.com"
        )
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            tokenType = "Bearer",
            expiresIn = 3600L,
            member = MemberInfo(
                id = member.id,
                email = member.email,
                nickname = member.nickname,
                profileImage = member.profileImage
            )
        )

        whenever(appleOauthClient.verifyAndGetUserInfo("apple_token")).thenReturn(userInfo)
        whenever(memberReader.findById("apple_apple.user.123456")).thenReturn(Optional.of(member))
        whenever(
            jwtTokenProvider.createLoginResponse(
                member,
                "test@privaterelay.appleid.com"
            )
        ).thenReturn(loginResponse)

        // when
        val result = authService.handleMobileLogin(request)

        // then
        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleMobileLogin - NAVER 로그인 시 예외가 발생한다`() {
        // given
        val request = MobileLoginRequest(provider = SocialProvider.NAVER, accessToken = "naver_token", idToken = null)

        // when & then
        val exception = assertThrows<MykkuException> {
            authService.handleMobileLogin(request)
        }
        assertEquals(ErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR, exception.errorCode)
    }

    @Test
    fun `handleGoogleMobileLogin - 새 사용자를 생성한다`() {
        // given
        val userInfo = GoogleUserInfo(
            id = "123456",
            email = "new@google.com",
            name = "New User",
            givenName = "New",
            familyName = "User",
            picture = "profile.jpg",
            verifiedEmail = true
        )
        val newMember = Member(
            id = "google_123456",
            nickname = "New User",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "new@google.com"
        )
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            tokenType = "Bearer",
            expiresIn = 3600L,
            member = MemberInfo(
                id = newMember.id,
                email = newMember.email,
                nickname = newMember.nickname,
                profileImage = newMember.profileImage
            )
        )

        whenever(googleOauthClient.verifyAndGetUserInfo("google_token")).thenReturn(userInfo)
        whenever(memberReader.findById("google_123456")).thenReturn(Optional.empty())
        whenever(memberWriter.save(any<Member>())).thenReturn(newMember)
        whenever(jwtTokenProvider.createLoginResponse(newMember, userInfo.email)).thenReturn(loginResponse)

        // when
        val request = MobileLoginRequest(provider = SocialProvider.GOOGLE, accessToken = "google_token", idToken = null)
        val result = authService.handleMobileLogin(request)

        // then
        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleKakaoMobileLogin - 이메일이 없는 경우 기본 이메일을 생성한다`() {
        // given
        val userInfo = KakaoUserInfo(
            id = 123456L,
            connectedAt = "2023-01-01T00:00:00Z",
            properties = KakaoUserInfo.Properties(
                nickname = "카카오유저",
                profileImage = "kakao_profile.jpg",
                thumbnailImage = null
            ),
            kakaoAccount = KakaoUserInfo.KakaoAccount(
                profileNicknameNeedsAgreement = false,
                profileImageNeedsAgreement = false,
                profile = KakaoUserInfo.KakaoAccount.Profile(
                    nickname = "카카오유저",
                    thumbnailImageUrl = null,
                    profileImageUrl = "kakao_profile.jpg",
                    isDefaultImage = false
                ),
                hasEmail = false,
                emailNeedsAgreement = false,
                isEmailValid = false,
                isEmailVerified = false,
                email = null
            )
        )
        val member = Member(
            id = "kakao_123456",
            nickname = "카카오유저",
            role = "USER",
            profileImage = "kakao_profile.jpg",
            provider = SocialProvider.KAKAO,
            socialId = "123456",
            email = "kakao_123456@kakao.com"
        )
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            tokenType = "Bearer",
            expiresIn = 3600L,
            member = MemberInfo(
                id = member.id,
                email = member.email,
                nickname = member.nickname,
                profileImage = member.profileImage
            )
        )

        whenever(kakaoOauthClient.verifyAndGetUserInfo("kakao_token")).thenReturn(userInfo)
        whenever(memberReader.findById("kakao_123456")).thenReturn(Optional.of(member))
        whenever(jwtTokenProvider.createLoginResponse(member, "kakao_123456@kakao.com")).thenReturn(loginResponse)

        // when
        val request = MobileLoginRequest(provider = SocialProvider.KAKAO, accessToken = "kakao_token", idToken = null)
        val result = authService.handleMobileLogin(request)

        // then
        assertEquals(loginResponse.accessToken, result.accessToken)
    }

    @Test
    fun `handleAppleMobileLogin - 이메일이 없는 경우 기본 이메일을 생성한다`() {
        // given
        val userInfo = AppleUserInfo(
            sub = "apple.user.123456",
            email = null
        )
        val member = Member(
            id = "apple_apple.user.123456",
            nickname = "애플사용자",
            role = "USER",
            profileImage = "",
            provider = SocialProvider.APPLE,
            socialId = "apple.user.123456",
            email = "apple_apple.user.123456@privaterelay.appleid.com"
        )
        val loginResponse = LoginResponse(
            accessToken = "jwt_token",
            tokenType = "Bearer",
            expiresIn = 3600L,
            member = MemberInfo(
                id = member.id,
                email = member.email,
                nickname = member.nickname,
                profileImage = member.profileImage
            )
        )

        whenever(appleOauthClient.verifyAndGetUserInfo("apple_token")).thenReturn(userInfo)
        whenever(memberReader.findById("apple_apple.user.123456")).thenReturn(Optional.of(member))
        whenever(
            jwtTokenProvider.createLoginResponse(
                member,
                "apple_apple.user.123456@privaterelay.appleid.com"
            )
        ).thenReturn(loginResponse)

        // when
        val request = MobileLoginRequest(provider = SocialProvider.APPLE, accessToken = null, idToken = "apple_token")
        val result = authService.handleMobileLogin(request)

        // then
        assertEquals(loginResponse.accessToken, result.accessToken)
    }
}

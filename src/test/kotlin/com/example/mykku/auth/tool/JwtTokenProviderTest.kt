package com.example.mykku.auth.tool

import com.example.mykku.auth.config.JwtProperties
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider
    private val testSecret = "test-secret-key-that-is-very-long-and-secure-for-testing-purposes-minimum-32-characters"

    @BeforeEach
    fun setUp() {
        val jwtProperties = JwtProperties(
            secret = testSecret,
            accessTokenExpiration = 86400000, // 24 hours
            refreshTokenExpiration = 1209600000 // 14 days
        )
        jwtTokenProvider = JwtTokenProvider(jwtProperties)
    }

    @Test
    fun `should generate valid token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"

        // when
        val token = jwtTokenProvider.generateAccessToken(memberId, email)

        // then
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `should validate correct token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val token = jwtTokenProvider.generateAccessToken(memberId, email)

        // when
        val isValid = jwtTokenProvider.validateToken(token)

        // then
        assertTrue(isValid)
    }

    @Test
    fun `should invalidate incorrect token`() {
        // given
        val invalidToken = "invalid.token.here"

        // when
        val isValid = jwtTokenProvider.validateToken(invalidToken)

        // then
        assertFalse(isValid)
    }

    @Test
    fun `should extract member id from token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val token = jwtTokenProvider.generateAccessToken(memberId, email)

        // when
        val extractedMemberId = jwtTokenProvider.getMemberIdFromToken(token)

        // then
        assertEquals(memberId, extractedMemberId)
    }

    @Test
    fun `should extract email from token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val token = jwtTokenProvider.generateAccessToken(memberId, email)

        // when
        val extractedEmail = jwtTokenProvider.getEmailFromToken(token)

        // then
        assertEquals(email, extractedEmail)
    }

    @Test
    fun `should generate valid refresh token`() {
        // given
        val memberId = "google_123456"

        // when
        val token = jwtTokenProvider.generateRefreshToken(memberId)

        // then
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `should identify refresh token correctly`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val refreshToken = jwtTokenProvider.generateRefreshToken(memberId)
        val accessToken = jwtTokenProvider.generateAccessToken(memberId, email)

        // when & then
        assertTrue(jwtTokenProvider.isRefreshToken(refreshToken))
        assertFalse(jwtTokenProvider.isRefreshToken(accessToken))
    }

    @Test
    fun `should get token type from token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val refreshToken = jwtTokenProvider.generateRefreshToken(memberId)
        val accessToken = jwtTokenProvider.generateAccessToken(memberId, email)

        // when
        val refreshTokenType = jwtTokenProvider.getTokenType(refreshToken)
        val accessTokenType = jwtTokenProvider.getTokenType(accessToken)

        // then
        assertEquals("refresh", refreshTokenType)
        assertEquals("access", accessTokenType)
    }

    @Test
    fun `should create login response`() {
        // given
        val member = Member(
            id = "google_123456",
            nickname = "testuser",
            role = "USER",
            profileImage = "https://example.com/profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )
        val userEmail = "test@example.com"

        // when
        val loginResponse = jwtTokenProvider.createLoginResponse(member, userEmail)

        // then
        assertNotNull(loginResponse.accessToken)
        assertNotNull(loginResponse.refreshToken)
        assertEquals(86400000, loginResponse.accessTokenExpiresIn)
        assertEquals(1209600000, loginResponse.refreshTokenExpiresIn)
        assertEquals(member.id, loginResponse.member.id)
        assertEquals(userEmail, loginResponse.member.email)
        assertEquals(member.nickname, loginResponse.member.nickname)
        assertEquals(member.profileImage, loginResponse.member.profileImage)
    }
}

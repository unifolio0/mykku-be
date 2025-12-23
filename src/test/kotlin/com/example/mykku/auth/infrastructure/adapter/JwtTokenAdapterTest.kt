package com.example.mykku.auth.infrastructure.adapter

import com.example.mykku.auth.config.JwtProperties
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.example.mykku.role.domain.Role

class JwtTokenAdapterTest {

    private lateinit var jwtTokenAdapter: JwtTokenAdapter
    private val testSecret = "test-secret-key-that-is-very-long-and-secure-for-testing-purposes-minimum-32-characters"

    @BeforeEach
    fun setUp() {
        val jwtProperties = JwtProperties(
            secret = testSecret,
            accessTokenExpiration = 86400000, // 24 hours
            refreshTokenExpiration = 1209600000 // 14 days
        )
        jwtTokenAdapter = JwtTokenAdapter(jwtProperties)
    }

    @Test
    fun `should generate valid token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"

        // when
        val token = jwtTokenAdapter.generateAccessToken(memberId, email)

        // then
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `should validate correct token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val token = jwtTokenAdapter.generateAccessToken(memberId, email)

        // when
        val isValid = jwtTokenAdapter.validateToken(token)

        // then
        assertTrue(isValid)
    }

    @Test
    fun `should invalidate incorrect token`() {
        // given
        val invalidToken = "invalid.token.here"

        // when
        val isValid = jwtTokenAdapter.validateToken(invalidToken)

        // then
        assertFalse(isValid)
    }

    @Test
    fun `should extract member id from token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val token = jwtTokenAdapter.generateAccessToken(memberId, email)

        // when
        val extractedMemberId = jwtTokenAdapter.getMemberIdFromToken(token)

        // then
        assertEquals(memberId, extractedMemberId)
    }

    @Test
    fun `should extract email from token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val token = jwtTokenAdapter.generateAccessToken(memberId, email)

        // when
        val extractedEmail = jwtTokenAdapter.getEmailFromToken(token)

        // then
        assertEquals(email, extractedEmail)
    }

    @Test
    fun `should generate valid refresh token`() {
        // given
        val memberId = "google_123456"

        // when
        val token = jwtTokenAdapter.generateRefreshToken(memberId)

        // then
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `should identify refresh token correctly`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val refreshToken = jwtTokenAdapter.generateRefreshToken(memberId)
        val accessToken = jwtTokenAdapter.generateAccessToken(memberId, email)

        // when & then
        assertTrue(jwtTokenAdapter.isRefreshToken(refreshToken))
        assertFalse(jwtTokenAdapter.isRefreshToken(accessToken))
    }

    @Test
    fun `should get token type from token`() {
        // given
        val memberId = "google_123456"
        val email = "test@example.com"
        val refreshToken = jwtTokenAdapter.generateRefreshToken(memberId)
        val accessToken = jwtTokenAdapter.generateAccessToken(memberId, email)

        // when
        val refreshTokenType = jwtTokenAdapter.getTokenType(refreshToken)
        val accessTokenType = jwtTokenAdapter.getTokenType(accessToken)

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
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "https://example.com/profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )
        val userEmail = "test@example.com"

        // when
        val loginResponse = jwtTokenAdapter.createLoginResponse(member, userEmail, true)

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

package com.example.mykku.auth.adapter.output.persistence

import com.example.mykku.auth.application.dto.LoginResult
import com.example.mykku.auth.application.dto.MemberInfoResult
import com.example.mykku.auth.application.port.output.TokenProvider
import com.example.mykku.auth.config.JwtProperties
import com.example.mykku.member.domain.entity.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProviderAdapter(
    val jwtProperties: JwtProperties
) : TokenProvider {

    private val logger = LoggerFactory.getLogger(JwtTokenProviderAdapter::class.java)
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    override fun generateAccessToken(memberId: String, email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts.builder()
            .subject(memberId)
            .claim("email", email)
            .claim("tokenType", "access")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    override fun generateRefreshToken(memberId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.refreshTokenExpiration)

        return Jwts.builder()
            .subject(memberId)
            .claim("tokenType", "refresh")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    override fun createLoginResult(member: Member, userEmail: String, isExistingUser: Boolean): LoginResult {
        val accessToken = generateAccessToken(member.id.value, userEmail)
        val refreshToken = generateRefreshToken(member.id.value)

        return LoginResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresIn = jwtProperties.accessTokenExpiration,
            refreshTokenExpiresIn = jwtProperties.refreshTokenExpiration,
            member = MemberInfoResult(
                memberId = member.memberId,
                email = userEmail,
                nickname = member.nickname,
                profileImage = member.profileImage
            ),
            isExistingUser = isExistingUser
        )
    }

    override fun validateToken(token: String): Boolean {
        return try {
            parseToken(token)
            true
        } catch (e: Exception) {
            logger.debug("Token validation failed", e)
            false
        }
    }

    override fun getMemberIdFromToken(token: String): String {
        val claims = parseToken(token)
        return claims.subject
    }

    override fun getEmailFromToken(token: String): String {
        val claims = parseToken(token)
        return claims.get("email", String::class.java)
    }

    override fun isRefreshToken(token: String): Boolean {
        return try {
            getTokenType(token) == "refresh"
        } catch (e: Exception) {
            false
        }
    }

    override fun getAccessTokenExpiration(): Long {
        return jwtProperties.accessTokenExpiration
    }

    private fun getTokenType(token: String): String? {
        val claims = parseToken(token)
        return claims.get("tokenType", String::class.java)
    }

    private fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}

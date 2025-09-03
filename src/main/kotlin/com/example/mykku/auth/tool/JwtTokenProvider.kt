package com.example.mykku.auth.tool

import com.example.mykku.auth.config.JwtProperties
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.member.domain.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    val jwtProperties: JwtProperties
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateAccessToken(memberId: String, email: String): String {
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

    fun generateRefreshToken(memberId: String): String {
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

    fun createLoginResponse(member: Member, userEmail: String): LoginResponse {
        val accessToken = generateAccessToken(member.id, userEmail)
        val refreshToken = generateRefreshToken(member.id)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresIn = jwtProperties.accessTokenExpiration,
            refreshTokenExpiresIn = jwtProperties.refreshTokenExpiration,
            member = MemberInfo(
                id = member.id,
                email = userEmail,
                nickname = member.nickname,
                profileImage = member.profileImage
            )
        )
    }

    fun validateToken(token: String): Boolean {
        return try {
            parseToken(token)
            true
        } catch (e: Exception) {
            logger.debug("Token validation failed", e)
            false
        }
    }

    fun getMemberIdFromToken(token: String): String {
        val claims = parseToken(token)
        return claims.subject
    }

    fun getEmailFromToken(token: String): String {
        val claims = parseToken(token)
        return claims.get("email", String::class.java)
    }

    fun getTokenType(token: String): String? {
        val claims = parseToken(token)
        return claims.get("tokenType", String::class.java)
    }

    fun isRefreshToken(token: String): Boolean {
        return try {
            getTokenType(token) == "refresh"
        } catch (e: Exception) {
            false
        }
    }

    private fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}

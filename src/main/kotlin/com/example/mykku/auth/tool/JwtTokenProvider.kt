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
    private val jwtProperties: JwtProperties
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateToken(memberId: String, email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.expiration)

        return Jwts.builder()
            .subject(memberId)
            .claim("email", email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    fun createLoginResponse(member: Member, userEmail: String): LoginResponse {
        val jwtToken = generateToken(member.id, userEmail)

        return LoginResponse(
            accessToken = jwtToken,
            expiresIn = jwtProperties.expiration,
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

    private fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}

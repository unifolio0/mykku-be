package com.example.mykku.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*

object TestTokenGenerator {
    
    private const val SECRET_KEY = "test-secret-key-should-be-very-long-and-secure-at-least-32-characters-long"
    private const val EXPIRATION_TIME = 86400000L // 24 hours
    
    private val secretKey = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())
    
    fun generateToken(memberId: String, email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + EXPIRATION_TIME)
        
        return Jwts.builder()
            .subject(memberId)
            .claim("email", email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }
    
    // data.sql의 샘플 멤버들에 대한 미리 생성된 토큰들
    object SampleTokens {
        val MEMBER1_TOKEN = generateToken("member1", "user1@example.com")
        val MEMBER2_TOKEN = generateToken("member2", "user2@example.com")
        val MEMBER3_TOKEN = generateToken("member3", "user3@example.com")
        val MEMBER4_TOKEN = generateToken("member4", "admin@example.com")
        val MEMBER5_TOKEN = generateToken("member5", "user5@example.com")
    }
    
    // 헤더에 추가할 수 있는 형태로 반환
    fun getBearerToken(memberId: String): String {
        val token = when (memberId) {
            "member1" -> SampleTokens.MEMBER1_TOKEN
            "member2" -> SampleTokens.MEMBER2_TOKEN
            "member3" -> SampleTokens.MEMBER3_TOKEN
            "member4" -> SampleTokens.MEMBER4_TOKEN
            "member5" -> SampleTokens.MEMBER5_TOKEN
            else -> generateToken(memberId, "$memberId@example.com")
        }
        return "Bearer $token"
    }
    
    // 테스트용 헤더 맵 생성
    fun createAuthHeaders(memberId: String): Map<String, String> {
        return mapOf("Authorization" to getBearerToken(memberId))
    }
}
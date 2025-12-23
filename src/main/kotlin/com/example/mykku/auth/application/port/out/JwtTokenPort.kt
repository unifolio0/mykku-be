package com.example.mykku.auth.application.port.out

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.member.domain.Member

interface JwtTokenPort {
    fun generateAccessToken(memberId: String, email: String): String
    fun generateRefreshToken(memberId: String): String
    fun createLoginResponse(member: Member, userEmail: String, isExistingUser: Boolean): LoginResponse
    fun validateToken(token: String): Boolean
    fun getMemberIdFromToken(token: String): String
    fun getEmailFromToken(token: String): String
    fun getTokenType(token: String): String?
    fun isRefreshToken(token: String): Boolean
    fun getAccessTokenExpiration(): Long
}

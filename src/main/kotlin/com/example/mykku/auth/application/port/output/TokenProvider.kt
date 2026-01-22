package com.example.mykku.auth.application.port.output

import com.example.mykku.auth.application.dto.LoginResult
import com.example.mykku.member.domain.entity.Member

interface TokenProvider {
    fun generateAccessToken(memberId: String, email: String): String
    fun generateRefreshToken(memberId: String): String
    fun validateToken(token: String): Boolean
    fun getMemberIdFromToken(token: String): String
    fun getEmailFromToken(token: String): String
    fun isRefreshToken(token: String): Boolean
    fun createLoginResult(member: Member, userEmail: String, isExistingUser: Boolean): LoginResult
    fun getAccessTokenExpiration(): Long
}

package com.example.mykku.auth.application.port.out

import com.example.mykku.member.domain.model.MemberId

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)

data class TokenPayload(
    val memberId: MemberId,
    val isExpired: Boolean
)

interface JwtTokenPort {
    fun generateTokenPair(memberId: MemberId): TokenPair
    fun generateAccessToken(memberId: MemberId): String
    fun generateRefreshToken(memberId: MemberId): String
    fun validateAccessToken(token: String): TokenPayload?
    fun validateRefreshToken(token: String): TokenPayload?
    fun getMemberIdFromToken(token: String): MemberId?
}

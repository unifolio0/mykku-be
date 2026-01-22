package com.example.mykku.auth.application.dto

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val accessTokenExpiresIn: Long,
    val refreshTokenExpiresIn: Long,
    val member: MemberInfoResult,
    val isExistingUser: Boolean
)

data class MemberInfoResult(
    val memberId: String,
    val email: String,
    val nickname: String,
    val profileImage: String?
)

data class RefreshTokenResult(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
)

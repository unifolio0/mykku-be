package com.example.mykku.auth.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val accessTokenExpiresIn: Long,
    val refreshTokenExpiresIn: Long,
    val member: MemberInfo
)

package com.example.mykku.auth.dto

data class LoginResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val member: MemberInfo
)

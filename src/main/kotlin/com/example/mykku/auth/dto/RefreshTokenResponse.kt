package com.example.mykku.auth.dto

data class RefreshTokenResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
)
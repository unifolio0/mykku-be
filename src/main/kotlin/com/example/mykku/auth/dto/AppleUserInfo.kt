package com.example.mykku.auth.dto

data class AppleUserInfo(
    val sub: String,
    val email: String?,
    val emailVerified: String? = "true",
    val isPrivateEmail: String? = "false"
)

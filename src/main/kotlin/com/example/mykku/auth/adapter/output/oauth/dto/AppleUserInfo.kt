package com.example.mykku.auth.adapter.output.oauth.dto

data class AppleUserInfo(
    val sub: String,
    val email: String?,
    val emailVerified: String? = "true",
    val isPrivateEmail: String? = "false"
)

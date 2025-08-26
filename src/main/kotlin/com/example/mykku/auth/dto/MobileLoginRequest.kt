package com.example.mykku.auth.dto

import com.example.mykku.member.domain.SocialProvider

data class MobileLoginRequest(
    val provider: SocialProvider,
    val accessToken: String,
    val idToken: String? = null // Apple의 경우 필요
)
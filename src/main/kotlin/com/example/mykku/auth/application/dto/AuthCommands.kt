package com.example.mykku.auth.application.dto

import com.example.mykku.member.domain.vo.SocialProvider

data class MobileLoginCommand(
    val provider: SocialProvider,
    val accessToken: String?,
    val idToken: String?
)

data class RefreshTokenCommand(
    val refreshToken: String
)

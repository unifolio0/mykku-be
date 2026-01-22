package com.example.mykku.auth.adapter.input.web.dto

import com.example.mykku.auth.application.dto.MobileLoginCommand
import com.example.mykku.auth.application.dto.RefreshTokenCommand
import com.example.mykku.member.domain.vo.SocialProvider

data class MobileLoginRequest(
    val provider: SocialProvider,
    val accessToken: String?,
    val idToken: String? = null
) {
    init {
        when (provider) {
            SocialProvider.GOOGLE, SocialProvider.KAKAO, SocialProvider.NAVER -> {
                require(!accessToken.isNullOrBlank()) {
                    "${provider.name} 로그인 시 accessToken은 필수입니다"
                }
            }
            SocialProvider.APPLE -> {
                require(!idToken.isNullOrBlank()) {
                    "Apple 로그인 시 idToken은 필수입니다"
                }
            }
            SocialProvider.EMAIL -> {
                throw IllegalArgumentException("EMAIL 제공자는 모바일 로그인을 지원하지 않습니다")
            }
        }
    }

    fun toCommand(): MobileLoginCommand {
        return MobileLoginCommand(
            provider = provider,
            accessToken = accessToken,
            idToken = idToken
        )
    }
}

data class RefreshTokenRequest(
    val refreshToken: String
) {
    fun toCommand(): RefreshTokenCommand {
        return RefreshTokenCommand(refreshToken = refreshToken)
    }
}

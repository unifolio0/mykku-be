package com.example.mykku.auth.dto

import com.example.mykku.member.domain.SocialProvider

data class MobileLoginRequest(
    val provider: SocialProvider,
    val accessToken: String?, // Google, Kakao에서 사용
    val idToken: String? = null // Apple에서 사용
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
        }
    }
}
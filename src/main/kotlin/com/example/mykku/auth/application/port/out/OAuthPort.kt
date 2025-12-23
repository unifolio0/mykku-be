package com.example.mykku.auth.application.port.out

data class OAuthUserInfo(
    val providerId: String,
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?
)

enum class OAuthProvider {
    KAKAO, NAVER, GOOGLE, APPLE
}

interface OAuthPort {
    fun getUserInfo(provider: OAuthProvider, accessToken: String): OAuthUserInfo
}

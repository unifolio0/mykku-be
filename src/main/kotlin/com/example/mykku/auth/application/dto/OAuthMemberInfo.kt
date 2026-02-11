package com.example.mykku.auth.application.dto

import com.example.mykku.member.domain.vo.SocialProvider

data class OAuthMemberInfo(
    val memberId: String,
    val profileImage: String,
    val provider: SocialProvider,
    val socialId: String,
    val email: String
)

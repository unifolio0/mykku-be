package com.example.mykku.auth.tool

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.member.domain.SocialProvider

interface OAuthLoginStrategy {
    fun supports(provider: SocialProvider): Boolean
    fun login(token: String): LoginResponse
}

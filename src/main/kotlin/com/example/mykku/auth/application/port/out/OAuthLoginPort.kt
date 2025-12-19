package com.example.mykku.auth.application.port.out

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.member.domain.SocialProvider

interface OAuthLoginPort {
    fun supports(provider: SocialProvider): Boolean
    fun login(token: String): LoginResponse
}

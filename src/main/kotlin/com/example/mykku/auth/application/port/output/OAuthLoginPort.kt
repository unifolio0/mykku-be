package com.example.mykku.auth.application.port.output

import com.example.mykku.auth.application.dto.LoginResult
import com.example.mykku.member.domain.vo.SocialProvider

interface OAuthLoginPort {
    fun supports(provider: SocialProvider): Boolean
    fun login(token: String): LoginResult
}

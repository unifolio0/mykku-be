package com.example.mykku.auth.infrastructure.adapter.oauth

import com.example.mykku.auth.application.port.out.JwtTokenPort
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.infrastructure.adapter.MemberOrchestrator
import com.example.mykku.auth.infrastructure.adapter.oauth.OAuthLoginStrategy
import com.example.mykku.auth.infrastructure.adapter.OAuthMemberExtractor
import com.example.mykku.auth.infrastructure.adapter.oauth.AppleOauthClient
import com.example.mykku.member.domain.SocialProvider
import org.springframework.stereotype.Component

@Component
class AppleLoginStrategy(
    private val appleOauthClient: AppleOauthClient,
    private val oauthMemberExtractor: OAuthMemberExtractor,
    private val memberOrchestrator: MemberOrchestrator,
    private val jwtTokenPort: JwtTokenPort
) : OAuthLoginStrategy {

    override fun supports(provider: SocialProvider): Boolean {
        return provider == SocialProvider.APPLE
    }

    override fun login(token: String): LoginResponse {
        val userInfo = appleOauthClient.verifyAndGetUserInfo(token)
        val memberInfo = oauthMemberExtractor.extractFromApple(userInfo)
        val (member, isExistingUser) = memberOrchestrator.findOrCreate(memberInfo)
        return jwtTokenPort.createLoginResponse(member, memberInfo.email, isExistingUser)
    }
}

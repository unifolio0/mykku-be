package com.example.mykku.auth.tool.strategy

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.auth.tool.MemberOrchestrator
import com.example.mykku.auth.tool.OAuthLoginStrategy
import com.example.mykku.auth.tool.OAuthMemberExtractor
import com.example.mykku.auth.tool.client.GoogleOauthClient
import com.example.mykku.member.domain.SocialProvider
import org.springframework.stereotype.Component

@Component
class GoogleLoginStrategy(
    private val googleOauthClient: GoogleOauthClient,
    private val oauthMemberExtractor: OAuthMemberExtractor,
    private val memberOrchestrator: MemberOrchestrator,
    private val jwtTokenProvider: JwtTokenProvider
) : OAuthLoginStrategy {

    override fun supports(provider: SocialProvider): Boolean {
        return provider == SocialProvider.GOOGLE
    }

    override fun login(token: String): LoginResponse {
        val userInfo = googleOauthClient.verifyAndGetUserInfo(token)
        val memberInfo = oauthMemberExtractor.extractFromGoogle(userInfo)
        val (member, isExistingUser) = memberOrchestrator.findOrCreate(memberInfo)
        return jwtTokenProvider.createLoginResponse(member, memberInfo.email, isExistingUser)
    }
}

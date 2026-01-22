package com.example.mykku.auth.adapter.output.oauth.strategy

import com.example.mykku.auth.adapter.output.oauth.OAuthMemberExtractor
import com.example.mykku.auth.adapter.output.oauth.client.AppleOauthClient
import com.example.mykku.auth.application.dto.LoginResult
import com.example.mykku.auth.application.port.output.MemberAuthPort
import com.example.mykku.auth.application.port.output.OAuthLoginPort
import com.example.mykku.auth.application.port.output.TokenProvider
import com.example.mykku.member.domain.vo.SocialProvider
import org.springframework.stereotype.Component

@Component
class AppleLoginAdapter(
    private val appleOauthClient: AppleOauthClient,
    private val oauthMemberExtractor: OAuthMemberExtractor,
    private val memberAuthPort: MemberAuthPort,
    private val tokenProvider: TokenProvider
) : OAuthLoginPort {

    override fun supports(provider: SocialProvider): Boolean {
        return provider == SocialProvider.APPLE
    }

    override fun login(token: String): LoginResult {
        val userInfo = appleOauthClient.verifyAndGetUserInfo(token)
        val memberInfo = oauthMemberExtractor.extractFromApple(userInfo)
        val (member, isExistingUser) = memberAuthPort.findOrCreate(memberInfo)
        return tokenProvider.createLoginResult(member, memberInfo.email, isExistingUser)
    }
}

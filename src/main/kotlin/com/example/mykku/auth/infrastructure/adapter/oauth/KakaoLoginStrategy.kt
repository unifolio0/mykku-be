package com.example.mykku.auth.infrastructure.adapter.oauth

import com.example.mykku.auth.application.port.out.JwtTokenPort
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.infrastructure.adapter.MemberOrchestrator
import com.example.mykku.auth.infrastructure.adapter.oauth.OAuthLoginStrategy
import com.example.mykku.auth.infrastructure.adapter.OAuthMemberExtractor
import com.example.mykku.auth.infrastructure.adapter.oauth.KakaoOauthClient
import com.example.mykku.member.domain.SocialProvider
import org.springframework.stereotype.Component

@Component
class KakaoLoginStrategy(
    private val kakaoOauthClient: KakaoOauthClient,
    private val oauthMemberExtractor: OAuthMemberExtractor,
    private val memberOrchestrator: MemberOrchestrator,
    private val jwtTokenPort: JwtTokenPort
) : OAuthLoginStrategy {

    override fun supports(provider: SocialProvider): Boolean {
        return provider == SocialProvider.KAKAO
    }

    override fun login(token: String): LoginResponse {
        val userInfo = kakaoOauthClient.verifyAndGetUserInfo(token)
        val memberInfo = oauthMemberExtractor.extractFromKakao(userInfo)
        val (member, isExistingUser) = memberOrchestrator.findOrCreate(memberInfo)
        return jwtTokenPort.createLoginResponse(member, memberInfo.email, isExistingUser)
    }
}

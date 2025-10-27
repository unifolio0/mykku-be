package com.example.mykku.auth.tool.strategy

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.auth.tool.MemberOrchestrator
import com.example.mykku.auth.tool.OAuthLoginStrategy
import com.example.mykku.auth.tool.OAuthMemberExtractor
import com.example.mykku.auth.tool.client.KakaoOauthClient
import com.example.mykku.member.domain.SocialProvider
import org.springframework.stereotype.Component

@Component
class KakaoLoginStrategy(
    private val kakaoOauthClient: KakaoOauthClient,
    private val oauthMemberExtractor: OAuthMemberExtractor,
    private val memberOrchestrator: MemberOrchestrator,
    private val jwtTokenProvider: JwtTokenProvider
) : OAuthLoginStrategy {

    override fun supports(provider: SocialProvider): Boolean {
        return provider == SocialProvider.KAKAO
    }

    override fun login(token: String): LoginResponse {
        val userInfo = kakaoOauthClient.verifyAndGetUserInfo(token)
        val memberInfo = oauthMemberExtractor.extractFromKakao(userInfo)
        val (member, isExistingUser) = memberOrchestrator.findOrCreate(memberInfo)
        return jwtTokenProvider.createLoginResponse(member, memberInfo.email, isExistingUser)
    }
}

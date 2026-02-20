package com.example.mykku.auth.adapter.output.oauth

import com.example.mykku.auth.adapter.output.oauth.dto.AppleUserInfo
import com.example.mykku.auth.adapter.output.oauth.dto.GoogleUserInfo
import com.example.mykku.auth.adapter.output.oauth.dto.KakaoUserInfo
import com.example.mykku.auth.adapter.output.oauth.dto.NaverUserInfo
import com.example.mykku.auth.application.dto.OAuthMemberInfo
import com.example.mykku.member.domain.vo.SocialProvider
import org.springframework.stereotype.Component

@Component
class OAuthMemberExtractor {

    fun extractFromGoogle(userInfo: GoogleUserInfo): OAuthMemberInfo {
        return OAuthMemberInfo(
            memberId = "google_${userInfo.id}",
            profileImage = userInfo.picture ?: "",
            provider = SocialProvider.GOOGLE,
            socialId = userInfo.id,
            email = userInfo.email
        )
    }

    fun extractFromKakao(userInfo: KakaoUserInfo): OAuthMemberInfo {
        return OAuthMemberInfo(
            memberId = "kakao_${userInfo.id}",
            profileImage = userInfo.properties?.profileImage
                ?: userInfo.kakaoAccount?.profile?.profileImageUrl
                ?: "",
            provider = SocialProvider.KAKAO,
            socialId = userInfo.id.toString(),
            email = userInfo.kakaoAccount?.email ?: "kakao_${userInfo.id}@kakao.com"
        )
    }

    fun extractFromApple(userInfo: AppleUserInfo): OAuthMemberInfo {
        return OAuthMemberInfo(
            memberId = "apple_${userInfo.sub}",
            profileImage = "",
            provider = SocialProvider.APPLE,
            socialId = userInfo.sub,
            email = userInfo.email ?: "apple_${userInfo.sub}@privaterelay.appleid.com"
        )
    }

    fun extractFromNaver(userInfo: NaverUserInfo): OAuthMemberInfo {
        return OAuthMemberInfo(
            memberId = "naver_${userInfo.response.id}",
            profileImage = userInfo.response.profileImage ?: "",
            provider = SocialProvider.NAVER,
            socialId = userInfo.response.id,
            email = userInfo.response.email ?: "naver_${userInfo.response.id}@naver.com"
        )
    }
}

package com.example.mykku.auth.service

import com.example.mykku.auth.dto.AppleUserInfo
import com.example.mykku.auth.dto.GoogleUserInfo
import com.example.mykku.auth.dto.KakaoUserInfo
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.tool.AppleOauthClient
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.auth.tool.KakaoOauthClient
import com.example.mykku.auth.tool.OauthClient
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.MemberWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberReader: MemberReader,
    private val memberWriter: MemberWriter,
    private val oauthClient: OauthClient,
    private val kakaoOauthClient: KakaoOauthClient,
    private val appleOauthClient: AppleOauthClient
) {
    fun getGoogleAuthUrl(): String {
        return oauthClient.getAuthUrl()
    }

    @Transactional
    fun handleGoogleCallback(code: String): LoginResponse {
        val tokenResponse = oauthClient.exchangeCodeForToken(code)
        val userInfo = oauthClient.getUserInfo(tokenResponse.accessToken)
        val member = createOrUpdateMember(userInfo)
        return jwtTokenProvider.createLoginResponse(member, userInfo.email)
    }

    fun getKakaoAuthUrl(): String {
        return kakaoOauthClient.getAuthUrl()
    }

    @Transactional
    fun handleKakaoCallback(code: String): LoginResponse {
        val tokenResponse = kakaoOauthClient.exchangeCodeForToken(code)
        val userInfo = kakaoOauthClient.getUserInfo(tokenResponse.accessToken)
        val member = createOrUpdateKakaoMember(userInfo)
        val email = userInfo.kakaoAccount?.email ?: "kakao_${userInfo.id}@kakao.com"
        return jwtTokenProvider.createLoginResponse(member, email)
    }

    private fun createOrUpdateMember(userInfo: GoogleUserInfo): Member {
        val memberId = "google_${userInfo.id}"

        return memberReader.findById(memberId).orElseGet {
            createNewMember(
                id = memberId,
                nickname = userInfo.name,
                profileImage = userInfo.picture ?: "",
                provider = SocialProvider.GOOGLE,
                socialId = userInfo.id,
                email = userInfo.email
            )
        }
    }

    private fun createOrUpdateKakaoMember(userInfo: KakaoUserInfo): Member {
        val memberId = "kakao_${userInfo.id}"
        val nickname = userInfo.properties?.nickname ?: userInfo.kakaoAccount?.profile?.nickname ?: "카카오사용자"
        val profileImage = userInfo.properties?.profileImage ?: userInfo.kakaoAccount?.profile?.profileImageUrl ?: ""
        val email = userInfo.kakaoAccount?.email ?: "kakao_${userInfo.id}@kakao.com"

        return memberReader.findById(memberId).orElseGet {
            createNewMember(
                id = memberId,
                nickname = nickname,
                profileImage = profileImage,
                provider = SocialProvider.KAKAO,
                socialId = userInfo.id.toString(),
                email = email
            )
        }
    }

    fun getAppleAuthUrl(state: String? = null): String {
        return if (state != null) {
            appleOauthClient.getAuthUrl(state)
        } else {
            appleOauthClient.getAuthUrl()
        }
    }

    @Transactional
    fun handleAppleCallback(code: String): LoginResponse {
        val tokenResponse = appleOauthClient.exchangeCodeForToken(code)
        val userInfo = appleOauthClient.getUserInfo(tokenResponse.idToken)
        val member = createOrUpdateAppleMember(userInfo)
        val email = userInfo.email ?: "apple_${userInfo.sub}@privaterelay.appleid.com"
        return jwtTokenProvider.createLoginResponse(member, email)
    }

    private fun createOrUpdateAppleMember(userInfo: AppleUserInfo): Member {
        val memberId = "apple_${userInfo.sub}"
        val nickname = "애플사용자"
        val email = userInfo.email ?: "apple_${userInfo.sub}@privaterelay.appleid.com"

        return memberReader.findById(memberId).orElseGet {
            createNewMember(
                id = memberId,
                nickname = nickname,
                profileImage = "",
                provider = SocialProvider.APPLE,
                socialId = userInfo.sub,
                email = email
            )
        }
    }

    private fun createNewMember(
        id: String,
        nickname: String,
        profileImage: String,
        provider: SocialProvider,
        socialId: String,
        email: String
    ): Member {
        return memberWriter.save(
            Member(
                id = id,
                nickname = nickname,
                role = "USER",
                profileImage = profileImage,
                provider = provider,
                socialId = socialId,
                email = email
            )
        )
    }
}

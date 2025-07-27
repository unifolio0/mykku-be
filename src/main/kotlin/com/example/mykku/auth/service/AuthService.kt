package com.example.mykku.auth.service

import com.example.mykku.auth.dto.GoogleUserInfo
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.tool.JwtTokenProvider
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
    private val oauthClient: OauthClient
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

    private fun createOrUpdateMember(userInfo: GoogleUserInfo): Member {
        val memberId = "google_${userInfo.id}"

        return memberReader.findById(memberId).orElseGet {
            memberWriter.save(
                Member(
                    id = memberId,
                    nickname = userInfo.name,
                    role = "USER",
                    profileImage = userInfo.picture ?: "",
                    provider = SocialProvider.GOOGLE,
                    socialId = userInfo.id,
                    email = userInfo.email
                )
            )
        }
    }
}

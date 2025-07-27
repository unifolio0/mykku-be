package com.example.mykku.auth.service

import com.example.mykku.auth.dto.GoogleUserInfo
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.auth.tool.OauthClient
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberRepository: MemberRepository,
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

        return memberRepository.findById(memberId).orElseGet {
            val nickname = generateUniqueNickname(userInfo.name)
            memberRepository.save(
                Member(
                    id = memberId,
                    nickname = nickname,
                    role = "USER",
                    profileImage = userInfo.picture ?: "",
                    provider = SocialProvider.GOOGLE,
                    socialId = userInfo.id,
                    email = userInfo.email
                )
            )
        }
    }

    private fun generateUniqueNickname(baseName: String): String {
        val cleanedName = baseName.replace(Regex("[^가-힣a-zA-Z0-9]"), "")
            .take(Member.NICKNAME_MAX_LENGTH)

        if (cleanedName.isEmpty()) {
            return generateRandomNickname()
        }

        var nickname = cleanedName
        var counter = 1
        while (memberRepository.existsByNickname(nickname)) {
            val suffix = counter.toString()
            nickname = cleanedName.take(Member.NICKNAME_MAX_LENGTH - suffix.length) + suffix
            counter++
        }

        return nickname
    }

    private fun generateRandomNickname(): String {
        val prefix = "user"
        val randomNum = (1000..9999).random()
        return "$prefix$randomNum"
    }
}

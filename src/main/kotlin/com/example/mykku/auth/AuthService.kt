package com.example.mykku.auth

import com.example.mykku.auth.dto.*
import com.example.mykku.auth.tool.AppleOauthClient
import com.example.mykku.auth.tool.GoogleOauthClient
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.auth.tool.KakaoOauthClient
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
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
    private val googleOauthClient: GoogleOauthClient,
    private val kakaoOauthClient: KakaoOauthClient,
    private val appleOauthClient: AppleOauthClient
) {

    @Transactional
    fun refreshAccessToken(request: RefreshTokenRequest): RefreshTokenResponse {
        validateRefreshToken(request.refreshToken)
        val memberId = jwtTokenProvider.getMemberIdFromToken(request.refreshToken)
        val member = memberReader.getMemberById(memberId)

        return RefreshTokenResponse(
            accessToken = jwtTokenProvider.generateAccessToken(member.id, member.email),
            expiresIn = jwtTokenProvider.jwtProperties.accessTokenExpiration
        )
    }

    @Transactional
    fun handleMobileLogin(request: MobileLoginRequest): LoginResponse {
        return when (request.provider) {
            SocialProvider.GOOGLE -> handleGoogleMobileLogin(request.accessToken!!)
            SocialProvider.KAKAO -> handleKakaoMobileLogin(request.accessToken!!)
            SocialProvider.APPLE -> handleAppleMobileLogin(request.idToken!!)
            SocialProvider.NAVER -> throw MykkuException(ErrorCode.OAUTH_EXTERNAL_SERVICE_ERROR)
        }
    }

    private fun validateRefreshToken(refreshToken: String) {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw MykkuException(ErrorCode.OAUTH_INVALID_TOKEN)
        }
    }

    private fun handleGoogleMobileLogin(accessToken: String): LoginResponse {
        val userInfo = googleOauthClient.verifyAndGetUserInfo(accessToken)
        val memberInfo = extractGoogleMemberInfo(userInfo)
        return processOAuthLogin(memberInfo)
    }

    private fun handleKakaoMobileLogin(accessToken: String): LoginResponse {
        val userInfo = kakaoOauthClient.verifyAndGetUserInfo(accessToken)
        val memberInfo = extractKakaoMemberInfo(userInfo)
        return processOAuthLogin(memberInfo)
    }

    private fun handleAppleMobileLogin(idToken: String): LoginResponse {
        val userInfo = appleOauthClient.verifyAndGetUserInfo(idToken)
        val memberInfo = extractAppleMemberInfo(userInfo)
        return processOAuthLogin(memberInfo)
    }

    private fun extractGoogleMemberInfo(userInfo: GoogleUserInfo): OAuthMemberInfo {
        return OAuthMemberInfo(
            memberId = "google_${userInfo.id}",
            nickname = userInfo.name,
            profileImage = userInfo.picture ?: "",
            provider = SocialProvider.GOOGLE,
            socialId = userInfo.id,
            email = userInfo.email
        )
    }

    private fun extractKakaoMemberInfo(userInfo: KakaoUserInfo): OAuthMemberInfo {
        return OAuthMemberInfo(
            memberId = "kakao_${userInfo.id}",
            nickname = userInfo.properties?.nickname
                ?: userInfo.kakaoAccount?.profile?.nickname
                ?: "카카오사용자",
            profileImage = userInfo.properties?.profileImage
                ?: userInfo.kakaoAccount?.profile?.profileImageUrl
                ?: "",
            provider = SocialProvider.KAKAO,
            socialId = userInfo.id.toString(),
            email = userInfo.kakaoAccount?.email ?: "kakao_${userInfo.id}@kakao.com"
        )
    }

    private fun extractAppleMemberInfo(userInfo: AppleUserInfo): OAuthMemberInfo {
        return OAuthMemberInfo(
            memberId = "apple_${userInfo.sub}",
            nickname = "애플사용자",
            profileImage = "",
            provider = SocialProvider.APPLE,
            socialId = userInfo.sub,
            email = userInfo.email ?: "apple_${userInfo.sub}@privaterelay.appleid.com"
        )
    }

    private fun processOAuthLogin(memberInfo: OAuthMemberInfo): LoginResponse {
        val (member, isExistingUser) = findOrCreateMember(memberInfo)
        return jwtTokenProvider.createLoginResponse(member, memberInfo.email, isExistingUser)
    }

    private fun findOrCreateMember(memberInfo: OAuthMemberInfo): Pair<Member, Boolean> {
        val existingMember = memberReader.findById(memberInfo.memberId)

        return if (existingMember.isPresent) {
            Pair(existingMember.get(), true)
        } else {
            val newMember = createNewMember(memberInfo)
            Pair(newMember, false)
        }
    }

    private fun createNewMember(memberInfo: OAuthMemberInfo): Member {
        return memberWriter.save(
            Member(
                id = memberInfo.memberId,
                nickname = memberInfo.nickname,
                role = "USER",
                profileImage = memberInfo.profileImage,
                provider = memberInfo.provider,
                socialId = memberInfo.socialId,
                email = memberInfo.email
            )
        )
    }

    private data class OAuthMemberInfo(
        val memberId: String,
        val nickname: String,
        val profileImage: String,
        val provider: SocialProvider,
        val socialId: String,
        val email: String
    )
}
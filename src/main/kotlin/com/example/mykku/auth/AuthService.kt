package com.example.mykku.auth

import com.example.mykku.auth.dto.*
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.auth.tool.OAuthLoginStrategy
import com.example.mykku.auth.exception.AuthException
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.model.MemberId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberQueryPort: MemberQueryPort,
    private val loginStrategies: List<OAuthLoginStrategy>
) {

    @Transactional
    fun refreshAccessToken(request: RefreshTokenRequest): RefreshTokenResponse {
        validateRefreshToken(request.refreshToken)
        val memberIdStr = jwtTokenProvider.getMemberIdFromToken(request.refreshToken)
        val member = memberQueryPort.getMemberById(MemberId(memberIdStr))

        return RefreshTokenResponse(
            accessToken = jwtTokenProvider.generateAccessToken(member.id, member.email),
            expiresIn = jwtTokenProvider.jwtProperties.accessTokenExpiration
        )
    }

    @Transactional
    fun handleMobileLogin(request: MobileLoginRequest): LoginResponse {
        val strategy = loginStrategies.firstOrNull { it.supports(request.provider) }
            ?: throw AuthException.oauthUserInfoFailed()

        val token = request.accessToken ?: request.idToken
            ?: throw AuthException.oauthInvalidToken()

        return strategy.login(token)
    }

    private fun validateRefreshToken(refreshToken: String) {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw AuthException.oauthInvalidToken()
        }
    }
}
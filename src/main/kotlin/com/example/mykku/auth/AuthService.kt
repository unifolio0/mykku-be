package com.example.mykku.auth

import com.example.mykku.auth.application.port.out.JwtTokenPort
import com.example.mykku.auth.application.port.out.OAuthLoginPort
import com.example.mykku.auth.dto.*
import com.example.mykku.auth.exception.AuthException
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.model.MemberId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtTokenPort: JwtTokenPort,
    private val memberQueryPort: MemberQueryPort,
    private val loginStrategies: List<OAuthLoginPort>
) {

    @Transactional
    fun refreshAccessToken(request: RefreshTokenRequest): RefreshTokenResponse {
        validateRefreshToken(request.refreshToken)
        val memberIdStr = jwtTokenPort.getMemberIdFromToken(request.refreshToken)
        val member = memberQueryPort.getMemberById(MemberId(memberIdStr))

        return RefreshTokenResponse(
            accessToken = jwtTokenPort.generateAccessToken(member.id, member.email),
            expiresIn = jwtTokenPort.getAccessTokenExpiration()
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
        if (!jwtTokenPort.validateToken(refreshToken) || !jwtTokenPort.isRefreshToken(refreshToken)) {
            throw AuthException.oauthInvalidToken()
        }
    }
}
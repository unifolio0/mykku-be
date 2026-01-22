package com.example.mykku.auth.application.usecase

import com.example.mykku.auth.application.dto.LoginResult
import com.example.mykku.auth.application.dto.MobileLoginCommand
import com.example.mykku.auth.application.port.input.MobileLoginUseCase
import com.example.mykku.auth.application.port.output.OAuthLoginPort
import com.example.mykku.auth.exception.AuthException
import com.example.mykku.member.domain.vo.SocialProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MobileLoginUseCaseImpl(
    private val oauthLoginPorts: List<OAuthLoginPort>
) : MobileLoginUseCase {

    override fun login(command: MobileLoginCommand): LoginResult {
        val oauthLoginPort = oauthLoginPorts.firstOrNull { it.supports(command.provider) }
            ?: throw AuthException.oauthUserInfoFailed()

        val token = resolveToken(command)
        return oauthLoginPort.login(token)
    }

    private fun resolveToken(command: MobileLoginCommand): String {
        return when (command.provider) {
            SocialProvider.GOOGLE, SocialProvider.KAKAO, SocialProvider.NAVER -> {
                command.accessToken ?: throw AuthException.oauthInvalidToken()
            }
            SocialProvider.APPLE -> {
                command.idToken ?: throw AuthException.oauthInvalidToken()
            }
            SocialProvider.EMAIL -> {
                throw AuthException.oauthUserInfoFailed()
            }
        }
    }
}

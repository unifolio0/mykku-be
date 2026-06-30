package com.example.mykku.auth.application.usecase

import com.example.mykku.auth.application.dto.LogoutCommand
import com.example.mykku.auth.application.port.input.LogoutUseCase
import com.example.mykku.notification.application.port.output.FcmTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LogoutUseCaseImpl(
    private val fcmTokenRepository: FcmTokenRepository
) : LogoutUseCase {

    override fun logout(command: LogoutCommand) {
        fcmTokenRepository.deleteByMemberIdAndDeviceId(command.memberId, command.deviceId)
    }
}

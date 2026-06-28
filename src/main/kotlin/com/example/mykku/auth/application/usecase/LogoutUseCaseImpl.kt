package com.example.mykku.auth.application.usecase

import com.example.mykku.auth.application.dto.LogoutCommand
import com.example.mykku.auth.application.port.input.LogoutUseCase
import com.example.mykku.notification.application.dto.DeleteFcmTokenCommand
import com.example.mykku.notification.application.port.input.ManageFcmTokenUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LogoutUseCaseImpl(
    private val manageFcmTokenUseCase: ManageFcmTokenUseCase
) : LogoutUseCase {

    override fun logout(command: LogoutCommand) {
        manageFcmTokenUseCase.deleteToken(DeleteFcmTokenCommand(command.memberId, command.deviceId))
    }
}

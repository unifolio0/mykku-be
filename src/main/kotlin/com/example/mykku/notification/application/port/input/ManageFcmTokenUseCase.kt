package com.example.mykku.notification.application.port.input

import com.example.mykku.notification.application.dto.DeleteFcmTokenCommand
import com.example.mykku.notification.application.dto.FcmTokenResult
import com.example.mykku.notification.application.dto.RegisterFcmTokenCommand

interface ManageFcmTokenUseCase {
    fun registerOrUpdateToken(command: RegisterFcmTokenCommand): FcmTokenResult
    fun getTokens(memberId: String): List<FcmTokenResult>
    fun deleteToken(command: DeleteFcmTokenCommand)
}

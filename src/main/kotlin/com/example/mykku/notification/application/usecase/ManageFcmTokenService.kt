package com.example.mykku.notification.application.usecase

import com.example.mykku.notification.application.dto.DeleteFcmTokenCommand
import com.example.mykku.notification.application.dto.FcmTokenResult
import com.example.mykku.notification.application.dto.RegisterFcmTokenCommand
import com.example.mykku.notification.application.port.input.ManageFcmTokenUseCase
import com.example.mykku.notification.application.port.output.FcmTokenRepository
import com.example.mykku.notification.domain.entity.FcmToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ManageFcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository
) : ManageFcmTokenUseCase {

    override fun registerOrUpdateToken(command: RegisterFcmTokenCommand): FcmTokenResult {
        releaseTokenFromPreviousOwner(command.token, command.memberId)

        val existingToken = fcmTokenRepository.findByMemberIdAndDeviceId(command.memberId, command.deviceId)

        val fcmToken = if (existingToken != null) {
            existingToken.updateToken(command.token)
            fcmTokenRepository.save(existingToken)
        } else {
            val newToken = FcmToken.create(
                memberId = command.memberId,
                token = command.token,
                deviceId = command.deviceId,
                deviceType = command.deviceType
            )
            fcmTokenRepository.save(newToken)
        }

        return FcmTokenResult.from(fcmToken)
    }

    @Transactional(readOnly = true)
    override fun getTokens(memberId: String): List<FcmTokenResult> {
        return fcmTokenRepository.findAllByMemberId(memberId).map { FcmTokenResult.from(it) }
    }

    override fun deleteToken(command: DeleteFcmTokenCommand) {
        fcmTokenRepository.deleteByMemberIdAndDeviceId(command.memberId, command.deviceId)
    }

    private fun releaseTokenFromPreviousOwner(token: String, currentMemberId: String) {
        fcmTokenRepository.findByToken(token)?.let { existingToken ->
            if (existingToken.memberId != currentMemberId) {
                fcmTokenRepository.deleteByToken(token)
            }
        }
    }
}

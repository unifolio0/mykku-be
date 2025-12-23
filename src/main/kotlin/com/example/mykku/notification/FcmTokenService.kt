package com.example.mykku.notification

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.port.out.FcmTokenQueryPort
import com.example.mykku.notification.application.port.out.FcmTokenRepositoryPort
import com.example.mykku.notification.dto.FcmTokenResponse
import com.example.mykku.notification.dto.RegisterFcmTokenRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmTokenService(
    private val fcmTokenQueryPort: FcmTokenQueryPort,
    private val fcmTokenRepositoryPort: FcmTokenRepositoryPort
) {

    @Transactional
    fun registerOrUpdateToken(
        member: Member,
        request: RegisterFcmTokenRequest
    ): FcmTokenResponse {
        val fcmToken = fcmTokenRepositoryPort.registerOrUpdateToken(
            member = member,
            token = request.token,
            deviceId = request.deviceId,
            deviceType = request.deviceType
        )

        return FcmTokenResponse.from(fcmToken)
    }

    @Transactional(readOnly = true)
    fun getTokens(member: Member): List<FcmTokenResponse> {
        val tokens = fcmTokenQueryPort.getTokensByMember(member)
        return tokens.map { FcmTokenResponse.from(it) }
    }

    @Transactional
    fun deleteToken(member: Member, deviceId: String) {
        fcmTokenRepositoryPort.deleteByMemberAndDeviceId(member, deviceId)
    }
}

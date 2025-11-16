package com.example.mykku.notification

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.dto.FcmTokenResponse
import com.example.mykku.notification.dto.RegisterFcmTokenRequest
import com.example.mykku.notification.tool.FcmTokenReader
import com.example.mykku.notification.tool.FcmTokenWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmTokenService(
    private val fcmTokenReader: FcmTokenReader,
    private val fcmTokenWriter: FcmTokenWriter
) {

    @Transactional
    fun registerOrUpdateToken(
        member: Member,
        request: RegisterFcmTokenRequest
    ): FcmTokenResponse {
        val fcmToken = fcmTokenWriter.registerOrUpdateToken(
            member = member,
            token = request.token,
            deviceId = request.deviceId,
            deviceType = request.deviceType
        )

        return FcmTokenResponse.from(fcmToken)
    }

    @Transactional(readOnly = true)
    fun getTokens(member: Member): List<FcmTokenResponse> {
        val tokens = fcmTokenReader.getTokensByMember(member)
        return tokens.map { FcmTokenResponse.from(it) }
    }

    @Transactional
    fun deleteToken(member: Member, deviceId: String) {
        fcmTokenWriter.deleteByMemberAndDeviceId(member, deviceId)
    }
}

package com.example.mykku.notification.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.port.out.FcmTokenQueryPort
import com.example.mykku.notification.application.port.out.FcmTokenRepositoryPort
import com.example.mykku.notification.domain.FcmToken
import com.example.mykku.notification.repository.FcmTokenRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FcmTokenRepositoryAdapter(
    private val fcmTokenRepository: FcmTokenRepository,
    private val fcmTokenQueryPort: FcmTokenQueryPort
) : FcmTokenRepositoryPort {

    @Transactional
    override fun registerOrUpdateToken(
        member: Member,
        token: String,
        deviceId: String,
        deviceType: String?
    ): FcmToken {
        releaseTokenFromPreviousOwner(token, member)

        val existingToken = fcmTokenQueryPort.getTokenByMemberAndDeviceId(member, deviceId)

        return if (existingToken != null) {
            existingToken.updateToken(token)
            existingToken
        } else {
            val fcmToken = FcmToken.create(
                member = member,
                token = token,
                deviceId = deviceId,
                deviceType = deviceType
            )
            fcmTokenRepository.save(fcmToken)
        }
    }

    private fun releaseTokenFromPreviousOwner(token: String, currentMember: Member) {
        fcmTokenQueryPort.getTokenByTokenString(token)?.let { existingToken ->
            if (existingToken.member.id != currentMember.id) {
                fcmTokenRepository.deleteByToken(token)
            }
        }
    }

    @Transactional
    override fun deleteToken(fcmToken: FcmToken) {
        fcmTokenRepository.delete(fcmToken)
    }

    @Transactional
    override fun deleteByMemberAndDeviceId(member: Member, deviceId: String) {
        fcmTokenRepository.deleteByMemberAndDeviceId(member, deviceId)
    }

    @Transactional
    override fun deleteAllByMember(member: Member) {
        fcmTokenRepository.deleteAllByMember(member)
    }
}

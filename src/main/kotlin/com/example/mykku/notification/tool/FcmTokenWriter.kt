package com.example.mykku.notification.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.FcmToken
import com.example.mykku.notification.repository.FcmTokenRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FcmTokenWriter(
    private val fcmTokenRepository: FcmTokenRepository,
    private val fcmTokenReader: FcmTokenReader
) {

    @Transactional
    fun registerOrUpdateToken(
        member: Member,
        token: String,
        deviceId: String,
        deviceType: String?
    ): FcmToken {
        releaseTokenFromPreviousOwner(token, member)

        val existingToken = fcmTokenReader.getTokenByMemberAndDeviceId(member, deviceId)

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
        fcmTokenReader.getTokenByTokenString(token)?.let { existingToken ->
            if (existingToken.member.id != currentMember.id) {
                fcmTokenRepository.deleteByToken(token)
            }
        }
    }

    @Transactional
    fun deleteToken(fcmToken: FcmToken) {
        fcmTokenRepository.delete(fcmToken)
    }

    @Transactional
    fun deleteByMemberAndDeviceId(member: Member, deviceId: String) {
        fcmTokenRepository.deleteByMemberAndDeviceId(member, deviceId)
    }

    @Transactional
    fun deleteAllByMember(member: Member) {
        fcmTokenRepository.deleteAllByMember(member)
    }
}

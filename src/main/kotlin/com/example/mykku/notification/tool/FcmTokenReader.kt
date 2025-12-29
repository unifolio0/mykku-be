package com.example.mykku.notification.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.FcmToken
import com.example.mykku.notification.exception.NotificationException
import com.example.mykku.notification.repository.FcmTokenRepository
import org.springframework.stereotype.Component

@Component
class FcmTokenReader(
    private val fcmTokenRepository: FcmTokenRepository
) {

    fun getFcmTokenById(id: Long): FcmToken {
        return fcmTokenRepository.findById(id)
            .orElseThrow { NotificationException.fcmTokenNotFound() }
    }

    fun getTokensByMember(member: Member): List<FcmToken> {
        return fcmTokenRepository.findAllByMember(member)
    }

    fun getTokenByMemberAndDeviceId(member: Member, deviceId: String): FcmToken? {
        return fcmTokenRepository.findByMemberAndDeviceId(member, deviceId).orElse(null)
    }

    fun getTokenByTokenString(token: String): FcmToken? {
        return fcmTokenRepository.findByToken(token).orElse(null)
    }

    fun existsByMemberAndDeviceId(member: Member, deviceId: String): Boolean {
        return fcmTokenRepository.existsByMemberAndDeviceId(member, deviceId)
    }
}

package com.example.mykku.notification.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.port.out.FcmTokenQueryPort
import com.example.mykku.notification.domain.FcmToken
import com.example.mykku.notification.exception.NotificationException
import com.example.mykku.notification.repository.FcmTokenRepository
import org.springframework.stereotype.Component

@Component
class FcmTokenQueryAdapter(
    private val fcmTokenRepository: FcmTokenRepository
) : FcmTokenQueryPort {

    override fun getFcmTokenById(id: Long): FcmToken {
        return fcmTokenRepository.findById(id)
            .orElseThrow { NotificationException.fcmTokenNotFound() }
    }

    override fun getTokensByMember(member: Member): List<FcmToken> {
        return fcmTokenRepository.findAllByMember(member)
    }

    override fun getTokenByMemberAndDeviceId(member: Member, deviceId: String): FcmToken? {
        return fcmTokenRepository.findByMemberAndDeviceId(member, deviceId).orElse(null)
    }

    override fun getTokenByTokenString(token: String): FcmToken? {
        return fcmTokenRepository.findByToken(token).orElse(null)
    }

    override fun existsByMemberAndDeviceId(member: Member, deviceId: String): Boolean {
        return fcmTokenRepository.existsByMemberAndDeviceId(member, deviceId)
    }
}

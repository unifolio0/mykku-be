package com.example.mykku.notification.infrastructure.adapter

import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.notification.application.port.out.NotificationQueryPort
import com.example.mykku.notification.repository.NotificationRepository
import org.springframework.stereotype.Component

@Component
class NotificationQueryAdapter(
    private val notificationRepository: NotificationRepository
) : NotificationQueryPort {

    override fun countUnreadByMemberId(memberId: MemberId): Int {
        return notificationRepository.countByReceiverIdAndIsReadFalse(memberId.value)
    }

    override fun hasUnreadNotifications(memberId: MemberId): Boolean {
        return notificationRepository.existsByReceiverIdAndIsReadFalse(memberId.value)
    }
}

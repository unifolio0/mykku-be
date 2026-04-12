package com.example.mykku.notification.application.usecase

import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.notification.application.dto.GetNotificationsQuery
import com.example.mykku.notification.application.dto.GetUnreadCountQuery
import com.example.mykku.notification.application.dto.GetUnreadNotificationsQuery
import com.example.mykku.notification.application.dto.NotificationResult
import com.example.mykku.notification.application.port.input.GetNotificationsUseCase
import com.example.mykku.notification.application.port.output.NotificationRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetNotificationsService(
    private val notificationRepository: NotificationRepository,
    private val memberRepository: MemberRepository
) : GetNotificationsUseCase {

    override fun getNotifications(query: GetNotificationsQuery): Page<NotificationResult> {
        val notifications = notificationRepository.findAllByReceiverId(query.memberId, query.pageable)
        return notifications.map { notification ->
            val sender = notification.senderId?.let { memberRepository.findById(MemberPk.of(it)) }
            NotificationResult.from(notification, sender?.nickname, sender?.profileImage)
        }
    }

    override fun getUnreadNotifications(query: GetUnreadNotificationsQuery): Page<NotificationResult> {
        val notifications = notificationRepository.findAllByReceiverIdAndIsRead(query.memberId, false, query.pageable)
        return notifications.map { notification ->
            val sender = notification.senderId?.let { memberRepository.findById(MemberPk.of(it)) }
            NotificationResult.from(notification, sender?.nickname, sender?.profileImage)
        }
    }

    override fun getUnreadCount(query: GetUnreadCountQuery): Long {
        return notificationRepository.countByReceiverIdAndIsRead(query.memberId, false)
    }
}

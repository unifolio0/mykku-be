package com.example.mykku.notification.application.usecase

import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.notification.application.dto.CreateNotificationCommand
import com.example.mykku.notification.application.dto.NotificationResult
import com.example.mykku.notification.application.port.input.SendNotificationUseCase
import com.example.mykku.notification.application.port.output.NotificationRepository
import com.example.mykku.notification.application.port.output.NotificationSettingRepository
import com.example.mykku.notification.domain.entity.Notification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SendNotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationSettingRepository: NotificationSettingRepository,
    private val fcmNotificationSender: FcmNotificationSender,
    private val memberRepository: MemberRepository
) : SendNotificationUseCase {

    @Transactional
    override fun createAndSendNotification(command: CreateNotificationCommand): NotificationResult? {
        val setting = notificationSettingRepository.findByMemberIdAndNotificationType(
            command.receiverId,
            command.type
        )
        if (setting != null && !setting.isEnabled) {
            return null
        }

        val notification = Notification.create(
            type = command.type,
            senderId = command.senderId,
            receiverId = command.receiverId,
            content = command.content,
            relatedResourceId = command.relatedResourceId,
            relatedResourceType = command.relatedResourceType
        )

        val savedNotification = notificationRepository.save(notification)

        fcmNotificationSender.send(command)

        val sender = command.senderId?.let { memberRepository.findById(MemberPk.of(it)) }
        return NotificationResult.from(savedNotification, sender?.nickname, sender?.profileImage)
    }
}

package com.example.mykku.notification.application.usecase

import com.example.mykku.notification.application.dto.MarkAllAsReadCommand
import com.example.mykku.notification.application.dto.MarkAsReadCommand
import com.example.mykku.notification.application.port.input.MarkNotificationReadUseCase
import com.example.mykku.notification.application.port.output.NotificationRepository
import com.example.mykku.notification.domain.vo.NotificationId
import com.example.mykku.notification.exception.NotificationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MarkNotificationReadService(
    private val notificationRepository: NotificationRepository
) : MarkNotificationReadUseCase {

    override fun markAsRead(command: MarkAsReadCommand) {
        val notification = notificationRepository.findById(NotificationId.of(command.notificationId))
            ?: throw NotificationException.notificationNotFound()

        if (notification.receiverId != command.memberId) {
            throw NotificationException.notificationNotAuthorized()
        }

        notification.markAsRead()
        notificationRepository.save(notification)
    }

    override fun markAllAsRead(command: MarkAllAsReadCommand) {
        if (command.category != null) {
            notificationRepository.markAllAsReadByReceiverIdAndTypeIn(command.memberId, command.category.types)
        } else {
            notificationRepository.markAllAsReadByReceiverId(command.memberId)
        }
    }
}

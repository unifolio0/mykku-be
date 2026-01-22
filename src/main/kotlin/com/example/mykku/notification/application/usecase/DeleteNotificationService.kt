package com.example.mykku.notification.application.usecase

import com.example.mykku.notification.application.dto.DeleteNotificationCommand
import com.example.mykku.notification.application.port.input.DeleteNotificationUseCase
import com.example.mykku.notification.application.port.output.NotificationRepository
import com.example.mykku.notification.domain.vo.NotificationId
import com.example.mykku.notification.exception.NotificationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteNotificationService(
    private val notificationRepository: NotificationRepository
) : DeleteNotificationUseCase {

    override fun deleteNotification(command: DeleteNotificationCommand) {
        val notification = notificationRepository.findById(NotificationId.of(command.notificationId))
            ?: throw NotificationException.notificationNotFound()

        if (notification.receiverId != command.memberId) {
            throw NotificationException.notificationNotAuthorized()
        }

        notificationRepository.delete(notification)
    }
}

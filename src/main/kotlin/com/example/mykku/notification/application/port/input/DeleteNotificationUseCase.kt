package com.example.mykku.notification.application.port.input

import com.example.mykku.notification.application.dto.DeleteNotificationCommand

interface DeleteNotificationUseCase {
    fun deleteNotification(command: DeleteNotificationCommand)
}

package com.example.mykku.notification.application.port.input

import com.example.mykku.notification.application.dto.CreateNotificationCommand
import com.example.mykku.notification.application.dto.NotificationResult

interface SendNotificationUseCase {
    fun createAndSendNotification(command: CreateNotificationCommand): NotificationResult?
}

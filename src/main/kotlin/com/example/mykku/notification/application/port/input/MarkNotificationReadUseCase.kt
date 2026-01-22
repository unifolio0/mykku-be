package com.example.mykku.notification.application.port.input

import com.example.mykku.notification.application.dto.MarkAllAsReadCommand
import com.example.mykku.notification.application.dto.MarkAsReadCommand

interface MarkNotificationReadUseCase {
    fun markAsRead(command: MarkAsReadCommand)
    fun markAllAsRead(command: MarkAllAsReadCommand)
}

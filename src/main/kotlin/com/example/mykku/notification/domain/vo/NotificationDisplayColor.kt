package com.example.mykku.notification.domain.vo

enum class NotificationDisplayColor {
    RED, GREEN, BLACK;

    companion object {
        fun defaultFor(type: NotificationType): NotificationDisplayColor = when (type) {
            NotificationType.FEED_LIKE -> BLACK
            NotificationType.FEED_COMMENT -> BLACK
            NotificationType.ROLE_EARNED -> BLACK
            NotificationType.SYSTEM_NOTICE -> BLACK
            NotificationType.CONTEST -> BLACK
            NotificationType.EVENT -> GREEN
        }
    }
}

package com.example.mykku.notification.domain.entity

import com.example.mykku.notification.domain.vo.NotificationSettingId
import com.example.mykku.notification.domain.vo.NotificationType
import java.time.LocalDateTime

class NotificationSetting private constructor(
    val id: NotificationSettingId?,
    val memberId: String,
    val notificationType: NotificationType,
    private var _isEnabled: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    val isEnabled: Boolean
        get() = _isEnabled

    fun enable() {
        _isEnabled = true
    }

    fun disable() {
        _isEnabled = false
    }

    fun updateEnabled(enabled: Boolean) {
        _isEnabled = enabled
    }

    companion object {
        fun create(
            memberId: String,
            notificationType: NotificationType,
            isEnabled: Boolean = true
        ): NotificationSetting {
            val now = LocalDateTime.now()
            return NotificationSetting(
                id = null,
                memberId = memberId,
                notificationType = notificationType,
                _isEnabled = isEnabled,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: NotificationSettingId,
            memberId: String,
            notificationType: NotificationType,
            isEnabled: Boolean,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): NotificationSetting {
            return NotificationSetting(
                id = id,
                memberId = memberId,
                notificationType = notificationType,
                _isEnabled = isEnabled,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }

        fun createDefaultSettings(memberId: String): List<NotificationSetting> {
            return NotificationType.entries.map { type ->
                create(memberId, type, true)
            }
        }
    }
}

package com.example.mykku.notification

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.port.out.NotificationSettingQueryPort
import com.example.mykku.notification.application.port.out.NotificationSettingRepositoryPort
import com.example.mykku.notification.dto.NotificationSettingResponse
import com.example.mykku.notification.dto.UpdateNotificationSettingRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationSettingService(
    private val notificationSettingQueryPort: NotificationSettingQueryPort,
    private val notificationSettingRepositoryPort: NotificationSettingRepositoryPort
) {

    @Transactional
    fun getOrCreateSettings(member: Member): List<NotificationSettingResponse> {
        var settings = notificationSettingQueryPort.getSettingsByMember(member)

        if (settings.isEmpty()) {
            settings = notificationSettingRepositoryPort.createDefaultSettings(member)
        }

        return settings.map { NotificationSettingResponse.from(it) }
    }

    @Transactional
    fun updateSetting(
        member: Member,
        request: UpdateNotificationSettingRequest
    ): NotificationSettingResponse {
        val setting = notificationSettingRepositoryPort.createOrUpdateSetting(
            member = member,
            notificationType = request.notificationType,
            isEnabled = request.isEnabled
        )

        return NotificationSettingResponse.from(setting)
    }
}

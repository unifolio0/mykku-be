package com.example.mykku.notification

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.dto.NotificationSettingResponse
import com.example.mykku.notification.dto.UpdateNotificationSettingRequest
import com.example.mykku.notification.tool.NotificationSettingReader
import com.example.mykku.notification.tool.NotificationSettingWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationSettingService(
    private val notificationSettingReader: NotificationSettingReader,
    private val notificationSettingWriter: NotificationSettingWriter
) {

    @Transactional(readOnly = true)
    fun getSettings(member: Member): List<NotificationSettingResponse> {
        var settings = notificationSettingReader.getSettingsByMember(member)

        if (settings.isEmpty()) {
            settings = notificationSettingWriter.createDefaultSettings(member)
        }

        return settings.map { NotificationSettingResponse.from(it) }
    }

    @Transactional
    fun updateSetting(
        member: Member,
        request: UpdateNotificationSettingRequest
    ): NotificationSettingResponse {
        val setting = notificationSettingWriter.createOrUpdateSetting(
            member = member,
            notificationType = request.notificationType,
            isEnabled = request.isEnabled
        )

        return NotificationSettingResponse.from(setting)
    }
}

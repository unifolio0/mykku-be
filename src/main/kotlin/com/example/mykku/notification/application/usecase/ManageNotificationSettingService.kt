package com.example.mykku.notification.application.usecase

import com.example.mykku.notification.application.dto.GetNotificationSettingsQuery
import com.example.mykku.notification.application.dto.NotificationSettingResult
import com.example.mykku.notification.application.dto.UpdateNotificationSettingCommand
import com.example.mykku.notification.application.port.input.ManageNotificationSettingUseCase
import com.example.mykku.notification.application.port.output.NotificationSettingRepository
import com.example.mykku.notification.domain.entity.NotificationSetting
import com.example.mykku.notification.domain.vo.NotificationType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ManageNotificationSettingService(
    private val notificationSettingRepository: NotificationSettingRepository
) : ManageNotificationSettingUseCase {

    override fun getOrCreateSettings(query: GetNotificationSettingsQuery): List<NotificationSettingResult> {
        var settings = notificationSettingRepository.findAllByMemberId(query.memberId)

        if (settings.isEmpty()) {
            val defaultSettings = NotificationSetting.createDefaultSettings(query.memberId)
            settings = notificationSettingRepository.saveAll(defaultSettings)
        } else {
            val existingTypes = settings.map { it.notificationType }.toSet()
            val missingTypes = NotificationType.entries.filter { it !in existingTypes }
            if (missingTypes.isNotEmpty()) {
                val newSettings = missingTypes.map { NotificationSetting.create(query.memberId, it) }
                val savedNewSettings = notificationSettingRepository.saveAll(newSettings)
                settings = settings + savedNewSettings
            }
        }

        return settings.map { NotificationSettingResult.from(it) }
    }

    override fun updateSetting(command: UpdateNotificationSettingCommand): NotificationSettingResult {
        val existingSetting = notificationSettingRepository.findByMemberIdAndNotificationType(
            command.memberId,
            command.notificationType
        )

        val setting = if (existingSetting != null) {
            existingSetting.updateEnabled(command.isEnabled)
            notificationSettingRepository.save(existingSetting)
        } else {
            val newSetting = NotificationSetting.create(
                memberId = command.memberId,
                notificationType = command.notificationType,
                isEnabled = command.isEnabled
            )
            notificationSettingRepository.save(newSetting)
        }

        return NotificationSettingResult.from(setting)
    }
}

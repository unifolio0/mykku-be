package com.example.mykku.notification.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.repository.NotificationSettingRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationSettingWriter(
    private val notificationSettingRepository: NotificationSettingRepository,
    private val notificationSettingReader: NotificationSettingReader
) {

    @Transactional
    fun createOrUpdateSetting(
        member: Member,
        notificationType: NotificationType,
        isEnabled: Boolean
    ): NotificationSetting {
        val existingSetting = notificationSettingReader.getSettingByMemberAndType(
            member,
            notificationType
        )

        return if (existingSetting != null) {
            existingSetting.updateEnabled(isEnabled)
            existingSetting
        } else {
            val setting = NotificationSetting.create(
                member = member,
                notificationType = notificationType,
                isEnabled = isEnabled
            )
            notificationSettingRepository.save(setting)
        }
    }

    @Transactional
    fun createDefaultSettings(member: Member): List<NotificationSetting> {
        val settings = NotificationSetting.createDefaultSettings(member)
        return notificationSettingRepository.saveAll(settings)
    }

    @Transactional
    fun updateSetting(setting: NotificationSetting, isEnabled: Boolean) {
        setting.updateEnabled(isEnabled)
    }

    @Transactional
    fun deleteAllByMember(member: Member) {
        notificationSettingRepository.deleteAllByMember(member)
    }
}

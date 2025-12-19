package com.example.mykku.notification.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.application.port.out.NotificationSettingQueryPort
import com.example.mykku.notification.application.port.out.NotificationSettingRepositoryPort
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType
import com.example.mykku.notification.repository.NotificationSettingRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationSettingRepositoryAdapter(
    private val notificationSettingRepository: NotificationSettingRepository,
    private val notificationSettingQueryPort: NotificationSettingQueryPort
) : NotificationSettingRepositoryPort {

    @Transactional
    override fun createOrUpdateSetting(
        member: Member,
        notificationType: NotificationType,
        isEnabled: Boolean
    ): NotificationSetting {
        val existingSetting = notificationSettingQueryPort.getSettingByMemberAndType(
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
    override fun createDefaultSettings(member: Member): List<NotificationSetting> {
        val settings = NotificationSetting.createDefaultSettings(member)
        return notificationSettingRepository.saveAll(settings)
    }

    @Transactional
    override fun updateSetting(setting: NotificationSetting, isEnabled: Boolean) {
        setting.updateEnabled(isEnabled)
    }

    @Transactional
    override fun deleteAllByMember(member: Member) {
        notificationSettingRepository.deleteAllByMember(member)
    }
}

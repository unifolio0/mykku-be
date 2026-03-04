package com.example.mykku.notification.application.port.output

import com.example.mykku.notification.domain.entity.NotificationSetting
import com.example.mykku.notification.domain.vo.NotificationSettingId
import com.example.mykku.notification.domain.vo.NotificationType

interface NotificationSettingRepository {
    fun save(setting: NotificationSetting): NotificationSetting
    fun saveAll(settings: List<NotificationSetting>): List<NotificationSetting>
    fun findById(id: NotificationSettingId): NotificationSetting?
    fun findAllByMemberId(memberId: Long): List<NotificationSetting>
    fun findByMemberIdAndNotificationType(memberId: Long, notificationType: NotificationType): NotificationSetting?
    fun existsByMemberIdAndNotificationType(memberId: Long, notificationType: NotificationType): Boolean
    fun delete(setting: NotificationSetting)
    fun deleteAllByMemberId(memberId: Long)
}

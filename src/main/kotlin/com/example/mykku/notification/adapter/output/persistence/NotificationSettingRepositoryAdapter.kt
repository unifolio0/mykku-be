package com.example.mykku.notification.adapter.output.persistence

import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.notification.adapter.output.persistence.entity.NotificationSettingJpaEntity
import com.example.mykku.notification.adapter.output.persistence.repository.NotificationSettingJpaRepository
import com.example.mykku.notification.application.port.output.NotificationSettingRepository
import com.example.mykku.notification.domain.entity.NotificationSetting
import com.example.mykku.notification.domain.vo.NotificationSettingId
import com.example.mykku.notification.domain.vo.NotificationType
import org.springframework.stereotype.Repository

@Repository
class NotificationSettingRepositoryAdapter(
    private val notificationSettingJpaRepository: NotificationSettingJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) : NotificationSettingRepository {

    override fun save(setting: NotificationSetting): NotificationSetting {
        val memberEntity = memberJpaRepository.findById(setting.memberId)
            .orElseThrow { IllegalArgumentException("Member not found: ${setting.memberId}") }

        val entity = if (setting.id != null) {
            val existingEntity = notificationSettingJpaRepository.findById(setting.id.value)
                .orElseThrow { IllegalArgumentException("NotificationSetting not found: ${setting.id.value}") }
            existingEntity.updateEnabled(setting.isEnabled)
            existingEntity
        } else {
            NotificationSettingJpaEntity.fromDomain(setting, memberEntity)
        }

        return notificationSettingJpaRepository.save(entity).toDomain()
    }

    override fun saveAll(settings: List<NotificationSetting>): List<NotificationSetting> {
        return settings.map { save(it) }
    }

    override fun findById(id: NotificationSettingId): NotificationSetting? {
        return notificationSettingJpaRepository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAllByMemberId(memberId: String): List<NotificationSetting> {
        return notificationSettingJpaRepository.findAllByMemberId(memberId).map { it.toDomain() }
    }

    override fun findByMemberIdAndNotificationType(memberId: String, notificationType: NotificationType): NotificationSetting? {
        return notificationSettingJpaRepository.findByMemberIdAndNotificationType(memberId, notificationType)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun existsByMemberIdAndNotificationType(memberId: String, notificationType: NotificationType): Boolean {
        return notificationSettingJpaRepository.existsByMemberIdAndNotificationType(memberId, notificationType)
    }

    override fun delete(setting: NotificationSetting) {
        setting.id?.let { id ->
            notificationSettingJpaRepository.deleteById(id.value)
        }
    }

    override fun deleteAllByMemberId(memberId: String) {
        notificationSettingJpaRepository.deleteAllByMemberId(memberId)
    }
}

package com.example.mykku.notification.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.NotificationSetting
import com.example.mykku.notification.domain.NotificationType
import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationSettingRepository : JpaRepository<NotificationSetting, Long> {
    fun findAllByMember(member: Member): List<NotificationSetting>

    fun findByMemberAndNotificationType(
        member: Member,
        notificationType: NotificationType
    ): Optional<NotificationSetting>

    fun deleteAllByMember(member: Member)

    fun existsByMemberAndNotificationType(
        member: Member,
        notificationType: NotificationType
    ): Boolean
}

package com.example.mykku.notification.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.notification.domain.entity.NotificationSetting
import com.example.mykku.notification.domain.vo.NotificationSettingId
import com.example.mykku.notification.domain.vo.NotificationType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "notification_setting",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["member_id", "notification_type"])
    ]
)
class NotificationSettingJpaEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberJpaEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    val notificationType: NotificationType,

    @Column(nullable = false)
    var isEnabled: Boolean = true
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun updateEnabled(enabled: Boolean) {
        this.isEnabled = enabled
    }

    fun toDomain(): NotificationSetting {
        return NotificationSetting.reconstitute(
            id = NotificationSettingId.of(id!!),
            memberId = member.id,
            notificationType = notificationType,
            isEnabled = isEnabled,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(setting: NotificationSetting, member: MemberJpaEntity): NotificationSettingJpaEntity {
            return NotificationSettingJpaEntity(
                member = member,
                notificationType = setting.notificationType,
                isEnabled = setting.isEnabled
            )
        }
    }
}

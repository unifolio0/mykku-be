package com.example.mykku.notification.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["member_id", "notification_type"])
    ]
)
class NotificationSetting(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    val notificationType: NotificationType,

    @Column(nullable = false)
    var isEnabled: Boolean = true
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun enable() {
        this.isEnabled = true
    }

    fun disable() {
        this.isEnabled = false
    }

    fun updateEnabled(enabled: Boolean) {
        this.isEnabled = enabled
    }

    companion object {
        fun create(
            member: Member,
            notificationType: NotificationType,
            isEnabled: Boolean = true
        ): NotificationSetting {
            return NotificationSetting(
                member = member,
                notificationType = notificationType,
                isEnabled = isEnabled
            )
        }

        fun createDefaultSettings(member: Member): List<NotificationSetting> {
            return NotificationType.entries.map { type ->
                create(member, type, true)
            }
        }
    }
}

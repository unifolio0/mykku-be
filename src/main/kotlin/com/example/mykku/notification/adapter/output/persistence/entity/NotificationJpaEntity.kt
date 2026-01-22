package com.example.mykku.notification.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.notification.domain.entity.Notification
import com.example.mykku.notification.domain.vo.NotificationId
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

@Entity
@Table(name = "notification")
class NotificationJpaEntity(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: NotificationType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    val sender: MemberJpaEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: MemberJpaEntity,

    @Column(nullable = false, length = 500)
    val content: String,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column
    val relatedResourceId: Long? = null,

    @Column(length = 100)
    val relatedResourceType: String? = null
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun markAsRead() {
        this.isRead = true
    }

    fun toDomain(): Notification {
        return Notification.reconstitute(
            id = NotificationId.of(id!!),
            type = type,
            senderId = sender?.id,
            receiverId = receiver.id,
            content = content,
            isRead = isRead,
            relatedResourceId = relatedResourceId,
            relatedResourceType = relatedResourceType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(
            notification: Notification,
            sender: MemberJpaEntity?,
            receiver: MemberJpaEntity
        ): NotificationJpaEntity {
            return NotificationJpaEntity(
                type = notification.type,
                sender = sender,
                receiver = receiver,
                content = notification.content,
                isRead = notification.isRead,
                relatedResourceId = notification.relatedResourceId,
                relatedResourceType = notification.relatedResourceType
            )
        }
    }
}

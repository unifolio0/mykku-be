package com.example.mykku.notification.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
class Notification(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: NotificationType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    val sender: Member?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: Member,

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

    companion object {
        fun create(
            type: NotificationType,
            sender: Member?,
            receiver: Member,
            content: String,
            relatedResourceId: Long? = null,
            relatedResourceType: String? = null
        ): Notification {
            return Notification(
                type = type,
                sender = sender,
                receiver = receiver,
                content = content,
                relatedResourceId = relatedResourceId,
                relatedResourceType = relatedResourceType
            )
        }
    }
}

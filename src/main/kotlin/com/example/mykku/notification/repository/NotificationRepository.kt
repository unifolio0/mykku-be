package com.example.mykku.notification.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.Notification
import com.example.mykku.notification.domain.NotificationType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findAllByReceiverOrderByCreatedAtDesc(receiver: Member, pageable: Pageable): Page<Notification>

    fun findAllByReceiverAndIsReadOrderByCreatedAtDesc(
        receiver: Member,
        isRead: Boolean,
        pageable: Pageable
    ): Page<Notification>

    fun findAllByReceiverAndTypeOrderByCreatedAtDesc(
        receiver: Member,
        type: NotificationType,
        pageable: Pageable
    ): Page<Notification>

    fun countByReceiverAndIsRead(receiver: Member, isRead: Boolean): Long

    fun deleteAllByReceiver(receiver: Member)
}

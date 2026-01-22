package com.example.mykku.notification.adapter.output.persistence

import com.example.mykku.member.adapter.output.persistence.MemberJpaRepository
import com.example.mykku.notification.adapter.output.persistence.entity.NotificationJpaEntity
import com.example.mykku.notification.adapter.output.persistence.repository.NotificationJpaRepository
import com.example.mykku.notification.application.port.output.NotificationRepository
import com.example.mykku.notification.domain.entity.Notification
import com.example.mykku.notification.domain.vo.NotificationId
import com.example.mykku.notification.domain.vo.NotificationType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class NotificationRepositoryAdapter(
    private val notificationJpaRepository: NotificationJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) : NotificationRepository {

    override fun save(notification: Notification): Notification {
        val receiverEntity = memberJpaRepository.findById(notification.receiverId)
            .orElseThrow { IllegalArgumentException("Receiver not found: ${notification.receiverId}") }

        val senderEntity = notification.senderId?.let { senderId ->
            memberJpaRepository.findById(senderId).orElse(null)
        }

        val entity = if (notification.id != null) {
            val existingEntity = notificationJpaRepository.findById(notification.id.value)
                .orElseThrow { IllegalArgumentException("Notification not found: ${notification.id.value}") }
            if (notification.isRead) {
                existingEntity.markAsRead()
            }
            existingEntity
        } else {
            NotificationJpaEntity.fromDomain(notification, senderEntity, receiverEntity)
        }

        return notificationJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: NotificationId): Notification? {
        return notificationJpaRepository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAllByReceiverId(receiverId: String, pageable: Pageable): Page<Notification> {
        return notificationJpaRepository.findAllByReceiverId(receiverId, pageable)
            .map { it.toDomain() }
    }

    override fun findAllByReceiverIdAndIsRead(receiverId: String, isRead: Boolean, pageable: Pageable): Page<Notification> {
        return notificationJpaRepository.findAllByReceiverIdAndIsRead(receiverId, isRead, pageable)
            .map { it.toDomain() }
    }

    override fun findAllByReceiverIdAndType(receiverId: String, type: NotificationType, pageable: Pageable): Page<Notification> {
        return notificationJpaRepository.findAllByReceiverIdAndType(receiverId, type, pageable)
            .map { it.toDomain() }
    }

    override fun countByReceiverIdAndIsRead(receiverId: String, isRead: Boolean): Long {
        return notificationJpaRepository.countByReceiverIdAndIsRead(receiverId, isRead)
    }

    override fun markAllAsReadByReceiverId(receiverId: String): Int {
        return notificationJpaRepository.markAllAsReadByReceiverId(receiverId)
    }

    override fun delete(notification: Notification) {
        notification.id?.let { id ->
            notificationJpaRepository.deleteById(id.value)
        }
    }

    override fun deleteAllByReceiverId(receiverId: String) {
        notificationJpaRepository.deleteAllByReceiverId(receiverId)
    }
}

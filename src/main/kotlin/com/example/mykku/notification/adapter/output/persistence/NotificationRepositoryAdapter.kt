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

    override fun findAllByReceiverId(receiverId: Long, pageable: Pageable): Page<Notification> {
        return notificationJpaRepository.findAllByReceiverId(receiverId, pageable)
            .map { it.toDomain() }
    }

    override fun findAllByReceiverIdAndIsRead(receiverId: Long, isRead: Boolean, pageable: Pageable): Page<Notification> {
        return notificationJpaRepository.findAllByReceiverIdAndIsRead(receiverId, isRead, pageable)
            .map { it.toDomain() }
    }

    override fun findAllByReceiverIdAndType(receiverId: Long, type: NotificationType, pageable: Pageable): Page<Notification> {
        return notificationJpaRepository.findAllByReceiverIdAndType(receiverId, type, pageable)
            .map { it.toDomain() }
    }

    override fun findAllByReceiverIdAndTypeIn(receiverId: Long, types: List<NotificationType>, pageable: Pageable): Page<Notification> {
        return notificationJpaRepository.findAllByReceiverIdAndTypeIn(receiverId, types, pageable)
            .map { it.toDomain() }
    }

    override fun findAllByReceiverIdAndIsReadAndTypeIn(receiverId: Long, isRead: Boolean, types: List<NotificationType>, pageable: Pageable): Page<Notification> {
        return notificationJpaRepository.findAllByReceiverIdAndIsReadAndTypeIn(receiverId, isRead, types, pageable)
            .map { it.toDomain() }
    }

    override fun countByReceiverIdAndIsRead(receiverId: Long, isRead: Boolean): Long {
        return notificationJpaRepository.countByReceiverIdAndIsRead(receiverId, isRead)
    }

    override fun countByReceiverIdAndIsReadAndTypeIn(receiverId: Long, isRead: Boolean, types: List<NotificationType>): Long {
        return notificationJpaRepository.countByReceiverIdAndIsReadAndTypeIn(receiverId, isRead, types)
    }

    override fun markAllAsReadByReceiverId(receiverId: Long): Int {
        return notificationJpaRepository.markAllAsReadByReceiverId(receiverId)
    }

    override fun markAllAsReadByReceiverIdAndTypeIn(receiverId: Long, types: List<NotificationType>): Int {
        return notificationJpaRepository.markAllAsReadByReceiverIdAndTypeIn(receiverId, types)
    }

    override fun delete(notification: Notification) {
        notification.id?.let { id ->
            notificationJpaRepository.deleteById(id.value)
        }
    }

    override fun deleteAllByReceiverId(receiverId: Long) {
        notificationJpaRepository.deleteAllByReceiverId(receiverId)
    }
}

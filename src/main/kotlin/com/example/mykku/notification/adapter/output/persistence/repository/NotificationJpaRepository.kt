package com.example.mykku.notification.adapter.output.persistence.repository

import com.example.mykku.notification.adapter.output.persistence.entity.NotificationJpaEntity
import com.example.mykku.notification.domain.vo.NotificationType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface NotificationJpaRepository : JpaRepository<NotificationJpaEntity, Long> {

    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.receiver.id = :receiverId ORDER BY n.createdAt DESC")
    fun findAllByReceiverId(@Param("receiverId") receiverId: String, pageable: Pageable): Page<NotificationJpaEntity>

    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.receiver.id = :receiverId AND n.isRead = :isRead ORDER BY n.createdAt DESC")
    fun findAllByReceiverIdAndIsRead(
        @Param("receiverId") receiverId: String,
        @Param("isRead") isRead: Boolean,
        pageable: Pageable
    ): Page<NotificationJpaEntity>

    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.receiver.id = :receiverId AND n.type = :type ORDER BY n.createdAt DESC")
    fun findAllByReceiverIdAndType(
        @Param("receiverId") receiverId: String,
        @Param("type") type: NotificationType,
        pageable: Pageable
    ): Page<NotificationJpaEntity>

    @Query("SELECT COUNT(n) FROM NotificationJpaEntity n WHERE n.receiver.id = :receiverId AND n.isRead = :isRead")
    fun countByReceiverIdAndIsRead(@Param("receiverId") receiverId: String, @Param("isRead") isRead: Boolean): Long

    @Modifying
    @Query("UPDATE NotificationJpaEntity n SET n.isRead = true WHERE n.receiver.id = :receiverId AND n.isRead = false")
    fun markAllAsReadByReceiverId(@Param("receiverId") receiverId: String): Int

    @Modifying
    @Query("DELETE FROM NotificationJpaEntity n WHERE n.receiver.id = :receiverId")
    fun deleteAllByReceiverId(@Param("receiverId") receiverId: String)
}

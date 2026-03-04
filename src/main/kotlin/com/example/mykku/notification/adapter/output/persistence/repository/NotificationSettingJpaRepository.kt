package com.example.mykku.notification.adapter.output.persistence.repository

import com.example.mykku.notification.adapter.output.persistence.entity.NotificationSettingJpaEntity
import com.example.mykku.notification.domain.vo.NotificationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface NotificationSettingJpaRepository : JpaRepository<NotificationSettingJpaEntity, Long> {

    @Query("SELECT s FROM NotificationSettingJpaEntity s WHERE s.member.id = :memberId")
    fun findAllByMemberId(@Param("memberId") memberId: Long): List<NotificationSettingJpaEntity>

    @Query("SELECT s FROM NotificationSettingJpaEntity s WHERE s.member.id = :memberId AND s.notificationType = :notificationType")
    fun findByMemberIdAndNotificationType(
        @Param("memberId") memberId: Long,
        @Param("notificationType") notificationType: NotificationType
    ): Optional<NotificationSettingJpaEntity>

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM NotificationSettingJpaEntity s WHERE s.member.id = :memberId AND s.notificationType = :notificationType")
    fun existsByMemberIdAndNotificationType(
        @Param("memberId") memberId: Long,
        @Param("notificationType") notificationType: NotificationType
    ): Boolean

    @Modifying
    @Query("DELETE FROM NotificationSettingJpaEntity s WHERE s.member.id = :memberId")
    fun deleteAllByMemberId(@Param("memberId") memberId: Long)
}

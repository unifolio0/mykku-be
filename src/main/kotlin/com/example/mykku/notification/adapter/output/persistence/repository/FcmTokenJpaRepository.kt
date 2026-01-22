package com.example.mykku.notification.adapter.output.persistence.repository

import com.example.mykku.notification.adapter.output.persistence.entity.FcmTokenJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface FcmTokenJpaRepository : JpaRepository<FcmTokenJpaEntity, Long> {

    @Query("SELECT f FROM FcmTokenJpaEntity f WHERE f.member.id = :memberId")
    fun findAllByMemberId(@Param("memberId") memberId: String): List<FcmTokenJpaEntity>

    @Query("SELECT f FROM FcmTokenJpaEntity f WHERE f.member.id = :memberId AND f.deviceId = :deviceId")
    fun findByMemberIdAndDeviceId(
        @Param("memberId") memberId: String,
        @Param("deviceId") deviceId: String
    ): Optional<FcmTokenJpaEntity>

    fun findByToken(token: String): Optional<FcmTokenJpaEntity>

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FcmTokenJpaEntity f WHERE f.member.id = :memberId AND f.deviceId = :deviceId")
    fun existsByMemberIdAndDeviceId(@Param("memberId") memberId: String, @Param("deviceId") deviceId: String): Boolean

    @Modifying
    @Query("DELETE FROM FcmTokenJpaEntity f WHERE f.member.id = :memberId AND f.deviceId = :deviceId")
    fun deleteByMemberIdAndDeviceId(@Param("memberId") memberId: String, @Param("deviceId") deviceId: String)

    @Modifying
    @Query("DELETE FROM FcmTokenJpaEntity f WHERE f.member.id = :memberId")
    fun deleteAllByMemberId(@Param("memberId") memberId: String)

    @Modifying
    fun deleteByToken(token: String)
}

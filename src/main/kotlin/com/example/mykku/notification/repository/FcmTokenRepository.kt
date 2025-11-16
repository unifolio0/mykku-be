package com.example.mykku.notification.repository

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.FcmToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FcmTokenRepository : JpaRepository<FcmToken, Long> {
    fun findAllByMember(member: Member): List<FcmToken>

    fun findByMemberAndDeviceId(member: Member, deviceId: String): Optional<FcmToken>

    fun findByToken(token: String): Optional<FcmToken>

    fun deleteByMemberAndDeviceId(member: Member, deviceId: String)

    fun deleteAllByMember(member: Member)

    fun deleteByToken(token: String)

    fun existsByMemberAndDeviceId(member: Member, deviceId: String): Boolean
}

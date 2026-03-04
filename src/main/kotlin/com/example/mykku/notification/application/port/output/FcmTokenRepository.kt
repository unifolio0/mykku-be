package com.example.mykku.notification.application.port.output

import com.example.mykku.notification.domain.entity.FcmToken
import com.example.mykku.notification.domain.vo.FcmTokenId

interface FcmTokenRepository {
    fun save(fcmToken: FcmToken): FcmToken
    fun findById(id: FcmTokenId): FcmToken?
    fun findAllByMemberId(memberId: Long): List<FcmToken>
    fun findByMemberIdAndDeviceId(memberId: Long, deviceId: String): FcmToken?
    fun findByToken(token: String): FcmToken?
    fun existsByMemberIdAndDeviceId(memberId: Long, deviceId: String): Boolean
    fun delete(fcmToken: FcmToken)
    fun deleteByMemberIdAndDeviceId(memberId: Long, deviceId: String)
    fun deleteAllByMemberId(memberId: Long)
    fun deleteByToken(token: String)
}

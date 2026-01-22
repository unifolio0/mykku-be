package com.example.mykku.notification.application.port.output

import com.example.mykku.notification.domain.entity.FcmToken
import com.example.mykku.notification.domain.vo.FcmTokenId

interface FcmTokenRepository {
    fun save(fcmToken: FcmToken): FcmToken
    fun findById(id: FcmTokenId): FcmToken?
    fun findAllByMemberId(memberId: String): List<FcmToken>
    fun findByMemberIdAndDeviceId(memberId: String, deviceId: String): FcmToken?
    fun findByToken(token: String): FcmToken?
    fun existsByMemberIdAndDeviceId(memberId: String, deviceId: String): Boolean
    fun delete(fcmToken: FcmToken)
    fun deleteByMemberIdAndDeviceId(memberId: String, deviceId: String)
    fun deleteAllByMemberId(memberId: String)
    fun deleteByToken(token: String)
}

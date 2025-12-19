package com.example.mykku.notification.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.FcmToken

interface FcmTokenQueryPort {
    fun getFcmTokenById(id: Long): FcmToken
    fun getTokensByMember(member: Member): List<FcmToken>
    fun getTokenByMemberAndDeviceId(member: Member, deviceId: String): FcmToken?
    fun getTokenByTokenString(token: String): FcmToken?
    fun existsByMemberAndDeviceId(member: Member, deviceId: String): Boolean
}

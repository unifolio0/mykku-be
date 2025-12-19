package com.example.mykku.notification.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.notification.domain.FcmToken

interface FcmTokenRepositoryPort {
    fun registerOrUpdateToken(member: Member, token: String, deviceId: String, deviceType: String?): FcmToken
    fun deleteToken(fcmToken: FcmToken)
    fun deleteByMemberAndDeviceId(member: Member, deviceId: String)
    fun deleteAllByMember(member: Member)
}

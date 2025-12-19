package com.example.mykku.notification.application.port.out

import com.example.mykku.member.domain.model.MemberId

interface NotificationQueryPort {
    fun countUnreadByMemberId(memberId: MemberId): Int
    fun hasUnreadNotifications(memberId: MemberId): Boolean
}

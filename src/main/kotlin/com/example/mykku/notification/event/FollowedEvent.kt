package com.example.mykku.notification.event

import com.example.mykku.member.domain.Member

data class FollowedEvent(
    val follower: Member,
    val following: Member
)

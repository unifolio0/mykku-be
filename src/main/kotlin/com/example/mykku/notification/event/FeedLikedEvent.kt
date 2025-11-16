package com.example.mykku.notification.event

import com.example.mykku.member.domain.Member

data class FeedLikedEvent(
    val feedId: Long,
    val feedAuthor: Member,
    val liker: Member
)

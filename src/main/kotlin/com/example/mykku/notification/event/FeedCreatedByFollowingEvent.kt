package com.example.mykku.notification.event

import com.example.mykku.member.domain.Member

data class FeedCreatedByFollowingEvent(
    val feedId: Long,
    val feedTitle: String,
    val author: Member,
    val followers: List<Member>
)

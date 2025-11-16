package com.example.mykku.notification.event

import com.example.mykku.member.domain.Member

data class FeedCommentedEvent(
    val feedId: Long,
    val feedAuthor: Member,
    val commenter: Member,
    val commentContent: String
)

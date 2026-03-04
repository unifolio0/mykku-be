package com.example.mykku.notification.application.event

data class FeedLikedEvent(
    val feedId: Long,
    val feedAuthorId: Long,
    val likerId: Long,
    val likerNickname: String
)

data class FeedCommentedEvent(
    val feedId: Long,
    val feedAuthorId: Long,
    val commenterId: Long,
    val commenterNickname: String,
    val commentContent: String
)

package com.example.mykku.notification.application.event

data class FeedLikedEvent(
    val feedId: Long,
    val feedAuthorId: String,
    val likerId: String,
    val likerNickname: String
)

data class FeedCommentedEvent(
    val feedId: Long,
    val feedAuthorId: String,
    val commenterId: String,
    val commenterNickname: String,
    val commentContent: String
)

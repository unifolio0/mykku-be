package com.example.mykku.feed.domain.model

@JvmInline
value class FeedCommentId(val value: Long) {
    init {
        require(value > 0) { "Feed Comment ID must be positive" }
    }
}

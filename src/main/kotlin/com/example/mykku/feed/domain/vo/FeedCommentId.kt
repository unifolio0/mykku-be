package com.example.mykku.feed.domain.vo

@JvmInline
value class FeedCommentId(val value: Long) {
    companion object {
        fun of(value: Long): FeedCommentId = FeedCommentId(value)
    }
}

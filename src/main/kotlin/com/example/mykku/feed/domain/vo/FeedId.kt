package com.example.mykku.feed.domain.vo

@JvmInline
value class FeedId(val value: Long) {
    companion object {
        fun of(value: Long): FeedId = FeedId(value)
    }
}

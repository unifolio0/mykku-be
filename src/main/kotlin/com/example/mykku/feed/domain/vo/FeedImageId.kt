package com.example.mykku.feed.domain.vo

@JvmInline
value class FeedImageId(val value: Long) {
    companion object {
        fun of(value: Long): FeedImageId = FeedImageId(value)
    }
}

package com.example.mykku.feed.domain.vo

@JvmInline
value class FeedTagId(val value: Long) {
    companion object {
        fun of(value: Long): FeedTagId = FeedTagId(value)
    }
}

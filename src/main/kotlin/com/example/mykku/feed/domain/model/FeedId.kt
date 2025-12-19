package com.example.mykku.feed.domain.model

@JvmInline
value class FeedId(val value: Long) {
    init {
        require(value > 0) { "Feed ID must be positive" }
    }
}

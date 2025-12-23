package com.example.mykku.feed.domain.model

@JvmInline
value class FeedContent(val value: String) {
    init {
        require(value.length <= MAX_LENGTH) { "Feed content must be $MAX_LENGTH characters or less" }
    }

    companion object {
        const val MAX_LENGTH = 1000
    }
}

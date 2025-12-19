package com.example.mykku.like.domain.model

@JvmInline
value class LikeFeedId(val value: Long) {
    init {
        require(value > 0) { "LikeFeed ID must be positive" }
    }
}

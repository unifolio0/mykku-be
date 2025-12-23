package com.example.mykku.like.domain.model

@JvmInline
value class LikeDailyMessageCommentId(val value: Long) {
    init {
        require(value > 0) { "LikeDailyMessageComment ID must be positive" }
    }
}

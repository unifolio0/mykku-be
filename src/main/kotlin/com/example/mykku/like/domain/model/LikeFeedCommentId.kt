package com.example.mykku.like.domain.model

@JvmInline
value class LikeFeedCommentId(val value: Long) {
    init {
        require(value > 0) { "LikeFeedComment ID must be positive" }
    }
}

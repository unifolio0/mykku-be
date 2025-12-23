package com.example.mykku.like.domain.model

@JvmInline
value class LikeBoardId(val value: Long) {
    init {
        require(value > 0) { "LikeBoard ID must be positive" }
    }
}

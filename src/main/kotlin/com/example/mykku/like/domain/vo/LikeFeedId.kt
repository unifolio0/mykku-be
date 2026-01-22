package com.example.mykku.like.domain.vo

@JvmInline
value class LikeFeedId(val value: Long) {
    companion object {
        fun of(value: Long): LikeFeedId = LikeFeedId(value)
    }
}

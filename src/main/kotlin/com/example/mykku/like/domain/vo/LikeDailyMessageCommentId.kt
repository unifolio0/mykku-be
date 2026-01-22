package com.example.mykku.like.domain.vo

@JvmInline
value class LikeDailyMessageCommentId(val value: Long) {
    companion object {
        fun of(value: Long): LikeDailyMessageCommentId = LikeDailyMessageCommentId(value)
    }
}

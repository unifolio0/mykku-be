package com.example.mykku.like.domain.vo

@JvmInline
value class LikeFeedCommentId(val value: Long) {
    companion object {
        fun of(value: Long): LikeFeedCommentId = LikeFeedCommentId(value)
    }
}

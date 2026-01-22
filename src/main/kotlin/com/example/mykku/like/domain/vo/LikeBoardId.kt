package com.example.mykku.like.domain.vo

@JvmInline
value class LikeBoardId(val value: Long) {
    companion object {
        fun of(value: Long): LikeBoardId = LikeBoardId(value)
    }
}

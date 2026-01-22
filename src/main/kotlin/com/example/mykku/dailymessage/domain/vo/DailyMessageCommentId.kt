package com.example.mykku.dailymessage.domain.vo

@JvmInline
value class DailyMessageCommentId(val value: Long) {
    companion object {
        fun of(value: Long): DailyMessageCommentId = DailyMessageCommentId(value)
    }
}

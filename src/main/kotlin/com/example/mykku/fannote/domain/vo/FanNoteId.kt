package com.example.mykku.fannote.domain.vo

@JvmInline
value class FanNoteId(val value: Long) {
    companion object {
        fun of(value: Long): FanNoteId = FanNoteId(value)
    }
}

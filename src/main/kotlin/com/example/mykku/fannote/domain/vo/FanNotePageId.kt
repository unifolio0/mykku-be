package com.example.mykku.fannote.domain.vo

@JvmInline
value class FanNotePageId(val value: Long) {
    companion object {
        fun of(value: Long): FanNotePageId = FanNotePageId(value)
    }
}

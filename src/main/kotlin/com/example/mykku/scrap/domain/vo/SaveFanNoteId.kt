package com.example.mykku.scrap.domain.vo

@JvmInline
value class SaveFanNoteId(val value: Long) {
    companion object {
        fun of(value: Long): SaveFanNoteId = SaveFanNoteId(value)
    }
}

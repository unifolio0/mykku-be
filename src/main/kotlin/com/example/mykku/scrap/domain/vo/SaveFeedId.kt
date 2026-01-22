package com.example.mykku.scrap.domain.vo

@JvmInline
value class SaveFeedId(val value: Long) {
    companion object {
        fun of(value: Long): SaveFeedId = SaveFeedId(value)
    }
}

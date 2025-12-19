package com.example.mykku.scrap.domain.model

@JvmInline
value class SaveFeedId(val value: Long) {
    init {
        require(value > 0) { "SaveFeed ID must be positive" }
    }
}

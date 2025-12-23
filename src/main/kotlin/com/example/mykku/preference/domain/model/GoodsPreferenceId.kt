package com.example.mykku.preference.domain.model

@JvmInline
value class GoodsPreferenceId(val value: Long) {
    init {
        require(value > 0) { "GoodsPreference ID must be positive" }
    }
}

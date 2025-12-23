package com.example.mykku.preference.domain.model

@JvmInline
value class MoodPreferenceId(val value: Long) {
    init {
        require(value > 0) { "MoodPreference ID must be positive" }
    }
}

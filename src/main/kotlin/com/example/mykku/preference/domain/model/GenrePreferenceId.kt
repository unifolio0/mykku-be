package com.example.mykku.preference.domain.model

@JvmInline
value class GenrePreferenceId(val value: Long) {
    init {
        require(value > 0) { "GenrePreference ID must be positive" }
    }
}

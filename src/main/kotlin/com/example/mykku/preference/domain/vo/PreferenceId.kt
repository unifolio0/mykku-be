package com.example.mykku.preference.domain.vo

@JvmInline
value class PreferenceId(val value: Long) {
    companion object {
        fun of(value: Long): PreferenceId = PreferenceId(value)
    }
}

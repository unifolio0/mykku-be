package com.example.mykku.achievement.domain.vo

@JvmInline
value class ActivityCountId(val value: Long) {
    companion object {
        fun of(value: Long): ActivityCountId = ActivityCountId(value)
    }
}

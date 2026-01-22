package com.example.mykku.contest.domain.vo

@JvmInline
value class ContestImageId(val value: Long) {
    companion object {
        fun of(value: Long): ContestImageId = ContestImageId(value)
    }
}

package com.example.mykku.contest.domain.vo

@JvmInline
value class ContestId(val value: Long) {
    companion object {
        fun of(value: Long): ContestId = ContestId(value)
    }
}

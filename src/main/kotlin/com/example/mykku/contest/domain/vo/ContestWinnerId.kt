package com.example.mykku.contest.domain.vo

@JvmInline
value class ContestWinnerId(val value: Long) {
    companion object {
        fun of(value: Long): ContestWinnerId = ContestWinnerId(value)
    }
}

package com.example.mykku.contest.domain.vo

@JvmInline
value class ContestTagId(val value: Long) {
    companion object {
        fun of(value: Long): ContestTagId = ContestTagId(value)
    }
}

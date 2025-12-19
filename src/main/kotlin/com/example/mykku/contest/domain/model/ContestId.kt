package com.example.mykku.contest.domain.model

@JvmInline
value class ContestId(val value: Long) {
    init {
        require(value > 0) { "Contest ID must be positive" }
    }
}

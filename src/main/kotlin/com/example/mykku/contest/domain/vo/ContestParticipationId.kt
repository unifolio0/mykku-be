package com.example.mykku.contest.domain.vo

@JvmInline
value class ContestParticipationId(val value: Long) {
    companion object {
        fun of(value: Long): ContestParticipationId = ContestParticipationId(value)
    }
}

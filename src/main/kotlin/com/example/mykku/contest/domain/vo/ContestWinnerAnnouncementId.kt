package com.example.mykku.contest.domain.vo

@JvmInline
value class ContestWinnerAnnouncementId(val value: Long) {
    companion object {
        fun of(value: Long): ContestWinnerAnnouncementId = ContestWinnerAnnouncementId(value)
    }
}

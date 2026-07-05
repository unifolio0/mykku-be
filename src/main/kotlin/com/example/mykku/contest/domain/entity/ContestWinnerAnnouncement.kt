package com.example.mykku.contest.domain.entity

import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestWinnerAnnouncementId
import java.time.LocalDate
import java.time.LocalDateTime

class ContestWinnerAnnouncement private constructor(
    val id: ContestWinnerAnnouncementId,
    val contestId: ContestId,
    val title: String,
    val content: String,
    val announcedAt: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            contestId: ContestId,
            title: String,
            content: String,
            announcedAt: LocalDate
        ): ContestWinnerAnnouncement {
            val now = LocalDateTime.now()
            return ContestWinnerAnnouncement(
                id = ContestWinnerAnnouncementId(0L),
                contestId = contestId,
                title = title,
                content = content,
                announcedAt = announcedAt,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: ContestWinnerAnnouncementId,
            contestId: ContestId,
            title: String,
            content: String,
            announcedAt: LocalDate,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): ContestWinnerAnnouncement {
            return ContestWinnerAnnouncement(
                id = id,
                contestId = contestId,
                title = title,
                content = content,
                announcedAt = announcedAt,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun update(
        title: String,
        content: String,
        announcedAt: LocalDate
    ): ContestWinnerAnnouncement {
        return ContestWinnerAnnouncement(
            id = this.id,
            contestId = this.contestId,
            title = title,
            content = content,
            announcedAt = announcedAt,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now()
        )
    }
}

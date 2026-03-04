package com.example.mykku.contest.domain.entity

import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestParticipationId
import java.time.LocalDateTime

class ContestParticipation private constructor(
    val id: ContestParticipationId,
    val contestId: ContestId,
    val feedId: Long,
    val memberId: Long?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            contestId: ContestId,
            feedId: Long,
            memberId: Long
        ): ContestParticipation {
            val now = LocalDateTime.now()
            return ContestParticipation(
                id = ContestParticipationId(0),
                contestId = contestId,
                feedId = feedId,
                memberId = memberId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: ContestParticipationId,
            contestId: ContestId,
            feedId: Long,
            memberId: Long?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): ContestParticipation {
            return ContestParticipation(
                id = id,
                contestId = contestId,
                feedId = feedId,
                memberId = memberId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

package com.example.mykku.scrap.domain.entity

import com.example.mykku.scrap.domain.vo.SaveContestId
import java.time.LocalDateTime

class SaveContestEntity private constructor(
    val id: SaveContestId?,
    val memberId: String,
    val contestId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: String,
            contestId: Long
        ): SaveContestEntity {
            val now = LocalDateTime.now()
            return SaveContestEntity(
                id = null,
                memberId = memberId,
                contestId = contestId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: Long,
            memberId: String,
            contestId: Long,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): SaveContestEntity {
            return SaveContestEntity(
                id = SaveContestId.of(id),
                memberId = memberId,
                contestId = contestId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

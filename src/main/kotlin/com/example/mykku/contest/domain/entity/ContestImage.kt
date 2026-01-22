package com.example.mykku.contest.domain.entity

import com.example.mykku.contest.domain.vo.ContestId
import com.example.mykku.contest.domain.vo.ContestImageId
import java.time.LocalDateTime

class ContestImage private constructor(
    val id: ContestImageId,
    val url: String,
    val orderIndex: Int,
    val contestId: ContestId,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            url: String,
            orderIndex: Int,
            contestId: ContestId
        ): ContestImage {
            val now = LocalDateTime.now()
            return ContestImage(
                id = ContestImageId(0),
                url = url,
                orderIndex = orderIndex,
                contestId = contestId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: ContestImageId,
            url: String,
            orderIndex: Int,
            contestId: ContestId,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): ContestImage {
            return ContestImage(
                id = id,
                url = url,
                orderIndex = orderIndex,
                contestId = contestId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

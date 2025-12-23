package com.example.mykku.scrap.domain.model

import com.example.mykku.contest.domain.model.ContestId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class SaveContestDomain private constructor(
    val id: SaveContestId?,
    val memberId: MemberId,
    val contestId: ContestId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            contestId: ContestId
        ): SaveContestDomain {
            return SaveContestDomain(
                id = null,
                memberId = memberId,
                contestId = contestId,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: SaveContestId,
            memberId: MemberId,
            contestId: ContestId,
            createdAt: Instant
        ): SaveContestDomain {
            return SaveContestDomain(
                id = id,
                memberId = memberId,
                contestId = contestId,
                createdAt = createdAt
            )
        }
    }
}

package com.example.mykku.scrap.domain.model

import com.example.mykku.fannote.domain.model.FanNoteId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class SaveFanNoteDomain private constructor(
    val id: SaveFanNoteId?,
    val memberId: MemberId,
    val fanNoteId: FanNoteId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            fanNoteId: FanNoteId
        ): SaveFanNoteDomain {
            return SaveFanNoteDomain(
                id = null,
                memberId = memberId,
                fanNoteId = fanNoteId,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: SaveFanNoteId,
            memberId: MemberId,
            fanNoteId: FanNoteId,
            createdAt: Instant
        ): SaveFanNoteDomain {
            return SaveFanNoteDomain(
                id = id,
                memberId = memberId,
                fanNoteId = fanNoteId,
                createdAt = createdAt
            )
        }
    }
}

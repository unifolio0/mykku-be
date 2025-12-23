package com.example.mykku.scrap.domain.model

import com.example.mykku.dailymessage.domain.model.DailyMessageId
import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class SaveDailyMessageDomain private constructor(
    val id: SaveDailyMessageId?,
    val memberId: MemberId,
    val dailyMessageId: DailyMessageId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            dailyMessageId: DailyMessageId
        ): SaveDailyMessageDomain {
            return SaveDailyMessageDomain(
                id = null,
                memberId = memberId,
                dailyMessageId = dailyMessageId,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: SaveDailyMessageId,
            memberId: MemberId,
            dailyMessageId: DailyMessageId,
            createdAt: Instant
        ): SaveDailyMessageDomain {
            return SaveDailyMessageDomain(
                id = id,
                memberId = memberId,
                dailyMessageId = dailyMessageId,
                createdAt = createdAt
            )
        }
    }
}

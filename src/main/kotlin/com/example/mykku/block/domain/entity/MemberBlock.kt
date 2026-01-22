package com.example.mykku.block.domain.entity

import com.example.mykku.block.domain.vo.MemberBlockId
import java.time.LocalDateTime

class MemberBlock private constructor(
    val id: MemberBlockId?,
    val blockerId: String,
    val blockedId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {

    companion object {
        fun create(
            blockerId: String,
            blockedId: String
        ): MemberBlock {
            val now = LocalDateTime.now()
            return MemberBlock(
                id = null,
                blockerId = blockerId,
                blockedId = blockedId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: MemberBlockId,
            blockerId: String,
            blockedId: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): MemberBlock {
            return MemberBlock(
                id = id,
                blockerId = blockerId,
                blockedId = blockedId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

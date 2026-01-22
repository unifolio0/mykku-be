package com.example.mykku.block.domain.entity

import com.example.mykku.block.domain.vo.KeywordBlockId
import java.time.LocalDateTime

class KeywordBlock private constructor(
    val id: KeywordBlockId?,
    val memberId: String,
    val keyword: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {

    companion object {
        const val KEYWORD_MAX_LENGTH = 50
        const val MAX_KEYWORD_COUNT = 100

        fun create(
            memberId: String,
            keyword: String
        ): KeywordBlock {
            require(keyword.length <= KEYWORD_MAX_LENGTH) {
                "Keyword exceeds maximum length of $KEYWORD_MAX_LENGTH characters"
            }

            val now = LocalDateTime.now()
            return KeywordBlock(
                id = null,
                memberId = memberId,
                keyword = keyword,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: KeywordBlockId,
            memberId: String,
            keyword: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): KeywordBlock {
            return KeywordBlock(
                id = id,
                memberId = memberId,
                keyword = keyword,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

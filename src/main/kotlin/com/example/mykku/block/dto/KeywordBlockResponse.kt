package com.example.mykku.block.dto

import com.example.mykku.block.domain.KeywordBlock
import java.time.LocalDateTime

data class KeywordBlockResponse(
    val id: Long,
    val keyword: String,
    val blockedAt: LocalDateTime
) {
    companion object {
        fun from(keywordBlock: KeywordBlock): KeywordBlockResponse {
            return KeywordBlockResponse(
                id = keywordBlock.id!!,
                keyword = keywordBlock.keyword,
                blockedAt = keywordBlock.createdAt
            )
        }
    }
}

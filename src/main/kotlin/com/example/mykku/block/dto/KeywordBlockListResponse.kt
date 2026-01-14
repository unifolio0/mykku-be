package com.example.mykku.block.dto

import com.example.mykku.block.domain.KeywordBlock
import org.springframework.data.domain.Page

data class KeywordBlockListResponse(
    val blocks: List<KeywordBlockResponse>,
    val totalCount: Long,
    val hasNext: Boolean
) {
    companion object {
        fun from(page: Page<KeywordBlock>): KeywordBlockListResponse {
            return KeywordBlockListResponse(
                blocks = page.content.map { KeywordBlockResponse.from(it) },
                totalCount = page.totalElements,
                hasNext = page.hasNext()
            )
        }
    }
}

package com.example.mykku.block.dto

import com.example.mykku.block.domain.MemberBlock
import org.springframework.data.domain.Page

data class MemberBlockListResponse(
    val blocks: List<MemberBlockResponse>,
    val totalCount: Long,
    val hasNext: Boolean
) {
    companion object {
        fun from(page: Page<MemberBlock>): MemberBlockListResponse {
            return MemberBlockListResponse(
                blocks = page.content.map { MemberBlockResponse.from(it) },
                totalCount = page.totalElements,
                hasNext = page.hasNext()
            )
        }
    }
}

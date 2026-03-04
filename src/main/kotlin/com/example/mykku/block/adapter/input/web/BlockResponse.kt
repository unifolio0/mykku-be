package com.example.mykku.block.adapter.input.web

import com.example.mykku.block.application.dto.KeywordBlockListResult
import com.example.mykku.block.application.dto.KeywordBlockResult
import com.example.mykku.block.application.dto.MemberBlockListResult
import com.example.mykku.block.application.dto.MemberBlockResult
import java.time.LocalDateTime

data class MemberBlockResponse(
    val id: Long,
    val blockedMemberId: Long,
    val blockedMemberNickname: String?,
    val blockedMemberProfileImage: String,
    val blockedAt: LocalDateTime
) {
    companion object {
        fun from(result: MemberBlockResult): MemberBlockResponse {
            return MemberBlockResponse(
                id = result.id,
                blockedMemberId = result.blockedMemberId,
                blockedMemberNickname = result.blockedMemberNickname,
                blockedMemberProfileImage = result.blockedMemberProfileImage,
                blockedAt = result.blockedAt
            )
        }
    }
}

data class MemberBlockListResponse(
    val blocks: List<MemberBlockResponse>,
    val totalCount: Long,
    val hasNext: Boolean
) {
    companion object {
        fun from(result: MemberBlockListResult): MemberBlockListResponse {
            return MemberBlockListResponse(
                blocks = result.blocks.map { MemberBlockResponse.from(it) },
                totalCount = result.totalCount,
                hasNext = result.hasNext
            )
        }
    }
}

data class KeywordBlockResponse(
    val id: Long,
    val keyword: String,
    val blockedAt: LocalDateTime
) {
    companion object {
        fun from(result: KeywordBlockResult): KeywordBlockResponse {
            return KeywordBlockResponse(
                id = result.id,
                keyword = result.keyword,
                blockedAt = result.blockedAt
            )
        }
    }
}

data class KeywordBlockListResponse(
    val blocks: List<KeywordBlockResponse>,
    val totalCount: Long,
    val hasNext: Boolean
) {
    companion object {
        fun from(result: KeywordBlockListResult): KeywordBlockListResponse {
            return KeywordBlockListResponse(
                blocks = result.blocks.map { KeywordBlockResponse.from(it) },
                totalCount = result.totalCount,
                hasNext = result.hasNext
            )
        }
    }
}

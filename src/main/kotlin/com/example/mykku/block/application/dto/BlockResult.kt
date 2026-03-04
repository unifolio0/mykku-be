package com.example.mykku.block.application.dto

import com.example.mykku.block.domain.entity.KeywordBlock
import com.example.mykku.block.domain.entity.MemberBlock
import java.time.LocalDateTime

data class MemberBlockResult(
    val id: Long,
    val blockedMemberId: Long,
    val blockedMemberNickname: String?,
    val blockedMemberProfileImage: String,
    val blockedAt: LocalDateTime
) {
    companion object {
        fun from(
            memberBlock: MemberBlock,
            blockedMemberNickname: String?,
            blockedMemberProfileImage: String
        ): MemberBlockResult {
            return MemberBlockResult(
                id = memberBlock.id!!.value,
                blockedMemberId = memberBlock.blockedId,
                blockedMemberNickname = blockedMemberNickname,
                blockedMemberProfileImage = blockedMemberProfileImage,
                blockedAt = memberBlock.createdAt
            )
        }
    }
}

data class MemberBlockListResult(
    val blocks: List<MemberBlockResult>,
    val totalCount: Long,
    val hasNext: Boolean
)

data class KeywordBlockResult(
    val id: Long,
    val keyword: String,
    val blockedAt: LocalDateTime
) {
    companion object {
        fun from(keywordBlock: KeywordBlock): KeywordBlockResult {
            return KeywordBlockResult(
                id = keywordBlock.id!!.value,
                keyword = keywordBlock.keyword,
                blockedAt = keywordBlock.createdAt
            )
        }
    }
}

data class KeywordBlockListResult(
    val blocks: List<KeywordBlockResult>,
    val totalCount: Long,
    val hasNext: Boolean
)

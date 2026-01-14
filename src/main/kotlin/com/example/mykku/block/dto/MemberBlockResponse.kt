package com.example.mykku.block.dto

import com.example.mykku.block.domain.MemberBlock
import java.time.LocalDateTime

data class MemberBlockResponse(
    val id: Long,
    val blockedMemberId: String,
    val blockedMemberNickname: String,
    val blockedMemberProfileImage: String,
    val blockedAt: LocalDateTime
) {
    companion object {
        fun from(memberBlock: MemberBlock): MemberBlockResponse {
            return MemberBlockResponse(
                id = memberBlock.id!!,
                blockedMemberId = memberBlock.blocked.id,
                blockedMemberNickname = memberBlock.blocked.nickname,
                blockedMemberProfileImage = memberBlock.blocked.profileImage,
                blockedAt = memberBlock.createdAt
            )
        }
    }
}

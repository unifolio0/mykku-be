package com.example.mykku.member.adapter.input.web.dto

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.role.adapter.input.web.RoleResponse
import java.time.LocalDateTime

data class MemberProfileResponse(
    val memberId: String?,
    val email: String,
    val nickname: String?,
    val profileImage: String,
    val role: RoleResponse?,
    val provider: String?,
    val emailVerified: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: MemberProfileResult): MemberProfileResponse {
            return MemberProfileResponse(
                memberId = result.memberId,
                email = result.email,
                nickname = result.nickname,
                profileImage = result.profileImage,
                role = result.role?.let { RoleResponse(it.id, it.name, it.description) },
                provider = result.provider,
                emailVerified = result.emailVerified,
                createdAt = result.createdAt
            )
        }
    }
}

data class CheckMemberIdResponse(
    val memberId: String,
    val available: Boolean
)

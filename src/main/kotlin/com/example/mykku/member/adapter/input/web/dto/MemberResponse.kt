package com.example.mykku.member.adapter.input.web.dto

import com.example.mykku.member.application.dto.MemberProfileResult
import java.time.LocalDateTime

data class MemberProfileResponse(
    val memberId: String,
    val email: String,
    val nickname: String,
    val profileImage: String,
    val role: String?,
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
                role = result.roleName,
                provider = result.provider,
                emailVerified = result.emailVerified,
                createdAt = result.createdAt
            )
        }
    }
}

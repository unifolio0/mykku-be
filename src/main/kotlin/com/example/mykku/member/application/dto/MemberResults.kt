package com.example.mykku.member.application.dto

import com.example.mykku.member.domain.entity.Member
import java.time.LocalDateTime

data class MemberProfileResult(
    val memberId: String,
    val email: String,
    val nickname: String,
    val profileImage: String,
    val roleId: Long?,
    val roleName: String?,
    val provider: String?,
    val emailVerified: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(member: Member, roleName: String?): MemberProfileResult {
            return MemberProfileResult(
                memberId = member.memberId,
                email = member.email,
                nickname = member.nickname,
                profileImage = member.profileImage,
                roleId = member.roleId,
                roleName = roleName,
                provider = member.provider?.name,
                emailVerified = member.emailVerified,
                createdAt = member.createdAt
            )
        }
    }
}

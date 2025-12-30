package com.example.mykku.member.dto

import com.example.mykku.member.domain.Member
import java.time.LocalDateTime

data class MemberProfileResponse(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImage: String,
    val role: String?,
    val provider: String?,
    val emailVerified: Boolean,
    val followerCount: Int,
    val followingCount: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(member: Member): MemberProfileResponse {
            return MemberProfileResponse(
                id = member.id,
                email = member.email,
                nickname = member.nickname,
                profileImage = member.profileImage,
                role = member.role?.name,
                provider = member.provider?.name,
                emailVerified = member.emailVerified,
                followerCount = member.followerCount,
                followingCount = member.followingCount,
                createdAt = member.createdAt
            )
        }
    }
}

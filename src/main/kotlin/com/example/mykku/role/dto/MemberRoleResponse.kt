package com.example.mykku.role.dto

import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import java.time.LocalDateTime

data class MemberRoleResponse(
    val id: Long,
    val role: RoleResponse,
    val isRepresentative: Boolean,
    val earnedAt: LocalDateTime
) {
    constructor(memberRole: MemberRole, member: Member) : this(
        id = memberRole.id!!,
        role = RoleResponse(memberRole.role),
        isRepresentative = member.role.id == memberRole.role.id,
        earnedAt = memberRole.createdAt
    )
}

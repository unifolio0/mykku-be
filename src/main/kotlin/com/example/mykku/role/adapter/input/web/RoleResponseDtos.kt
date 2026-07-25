package com.example.mykku.role.adapter.input.web

import com.example.mykku.role.application.dto.AcquireRoleResult
import com.example.mykku.role.application.dto.MemberRoleResult
import com.example.mykku.role.application.dto.RoleResult
import java.time.LocalDateTime

data class RoleResponse(
    val id: Long,
    val name: String,
    val description: String?
) {
    companion object {
        fun from(result: RoleResult): RoleResponse = RoleResponse(
            id = result.id,
            name = result.name,
            description = result.description
        )
    }
}

data class MemberRoleResponse(
    val id: Long,
    val role: RoleResponse,
    val isRepresentative: Boolean,
    val earnedAt: LocalDateTime
) {
    companion object {
        fun from(result: MemberRoleResult): MemberRoleResponse = MemberRoleResponse(
            id = result.id,
            role = RoleResponse.from(result.role),
            isRepresentative = result.isRepresentative,
            earnedAt = result.earnedAt
        )
    }
}

data class AcquireRoleResponse(
    val acquired: Boolean,
    val memberRole: MemberRoleResponse
) {
    companion object {
        fun from(result: AcquireRoleResult): AcquireRoleResponse = AcquireRoleResponse(
            acquired = result.acquired,
            memberRole = MemberRoleResponse.from(result.memberRole)
        )
    }
}

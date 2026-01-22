package com.example.mykku.role.domain.entity

import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.domain.vo.RoleId
import java.time.LocalDateTime

class MemberRole private constructor(
    val id: MemberRoleId,
    val memberId: String,
    val roleId: RoleId,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: String,
            roleId: RoleId
        ): MemberRole {
            val now = LocalDateTime.now()
            return MemberRole(
                id = MemberRoleId(0),
                memberId = memberId,
                roleId = roleId,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: MemberRoleId,
            memberId: String,
            roleId: RoleId,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): MemberRole {
            return MemberRole(
                id = id,
                memberId = memberId,
                roleId = roleId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

package com.example.mykku.role.domain.entity

import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.domain.vo.RoleId
import java.time.LocalDateTime

class MemberRole private constructor(
    val id: MemberRoleId,
    val memberId: Long,
    val roleId: RoleId,
    val checkedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            memberId: Long,
            roleId: RoleId
        ): MemberRole {
            val now = LocalDateTime.now()
            return MemberRole(
                id = MemberRoleId(0),
                memberId = memberId,
                roleId = roleId,
                checkedAt = null,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: MemberRoleId,
            memberId: Long,
            roleId: RoleId,
            checkedAt: LocalDateTime?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): MemberRole = MemberRole(id, memberId, roleId, checkedAt, createdAt, updatedAt)
    }
}

package com.example.mykku.role.domain.model

import com.example.mykku.member.domain.model.MemberId
import java.time.Instant

class MemberRoleDomain private constructor(
    val id: MemberRoleId?,
    val memberId: MemberId,
    val roleId: RoleId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            roleId: RoleId
        ): MemberRoleDomain {
            return MemberRoleDomain(
                id = null,
                memberId = memberId,
                roleId = roleId,
                createdAt = Instant.now()
            )
        }

        fun reconstitute(
            id: MemberRoleId,
            memberId: MemberId,
            roleId: RoleId,
            createdAt: Instant
        ): MemberRoleDomain {
            return MemberRoleDomain(
                id = id,
                memberId = memberId,
                roleId = roleId,
                createdAt = createdAt
            )
        }
    }
}

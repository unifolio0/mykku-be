package com.example.mykku.role.application.port.out

import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.role.domain.model.RoleId

data class RoleSummary(
    val id: Long,
    val name: String,
    val description: String?
)

interface RoleQueryPort {
    fun findById(id: RoleId): RoleSummary?
    fun findByName(name: String): RoleSummary?
    fun existsById(id: RoleId): Boolean
    fun getMemberRoles(memberId: MemberId): List<RoleSummary>
    fun hasMemberRole(memberId: MemberId, roleName: String): Boolean
}

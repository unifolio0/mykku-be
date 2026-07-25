package com.example.mykku.role.application.port.output

import com.example.mykku.role.domain.entity.MemberRole
import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.domain.vo.RoleId

interface MemberRoleRepository {
    fun save(memberRole: MemberRole): MemberRole
    fun saveIfAbsent(memberId: Long, roleId: RoleId)
    fun findById(id: MemberRoleId): MemberRole?
    fun findByMemberId(memberId: Long): List<MemberRole>
    fun findByMemberIdWithRole(memberId: Long): List<MemberRoleWithRole>
    fun findByMemberIdAndRoleId(memberId: Long, roleId: RoleId): MemberRole?
    fun existsByRoleId(roleId: RoleId): Boolean
    fun delete(memberRole: MemberRole)
}

data class MemberRoleWithRole(
    val memberRole: MemberRole,
    val role: com.example.mykku.role.domain.entity.Role
)

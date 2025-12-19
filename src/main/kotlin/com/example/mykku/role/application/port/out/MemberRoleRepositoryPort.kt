package com.example.mykku.role.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role

interface MemberRoleRepositoryPort {
    fun assignRole(member: Member, role: Role): MemberRole
    fun delete(memberRole: MemberRole)
}

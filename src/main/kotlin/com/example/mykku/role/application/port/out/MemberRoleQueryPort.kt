package com.example.mykku.role.application.port.out

import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role

interface MemberRoleQueryPort {
    fun getMemberRolesByMember(member: Member): List<MemberRole>
    fun getMemberRoleById(memberRoleId: Long, member: Member): MemberRole
    fun existsByRole(role: Role): Boolean
}

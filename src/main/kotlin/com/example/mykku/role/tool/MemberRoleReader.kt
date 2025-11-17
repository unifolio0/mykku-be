package com.example.mykku.role.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.repository.MemberRoleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class MemberRoleReader(
    private val memberRoleRepository: MemberRoleRepository
) {
    fun getMemberRolesByMember(member: Member): List<MemberRole> {
        return memberRoleRepository.findByMemberWithRole(member)
    }

    fun getMemberRoleById(memberRoleId: Long, member: Member): MemberRole {
        val memberRole = memberRoleRepository.findByIdOrNull(memberRoleId)
            ?: throw RoleException.memberRoleNotFound()

        if (memberRole.member.id != member.id) {
            throw RoleException.memberRoleUnauthorized()
        }

        return memberRole
    }

    fun existsByRole(role: Role): Boolean {
        return memberRoleRepository.existsByRole(role)
    }
}

package com.example.mykku.role.infrastructure.adapter

import com.example.mykku.member.domain.Member
import com.example.mykku.role.application.port.out.MemberRoleRepositoryPort
import com.example.mykku.role.domain.MemberRole
import com.example.mykku.role.domain.Role
import com.example.mykku.role.exception.RoleException
import com.example.mykku.role.repository.MemberRoleRepository
import org.springframework.stereotype.Component

@Component
class MemberRoleRepositoryAdapter(
    private val memberRoleRepository: MemberRoleRepository
) : MemberRoleRepositoryPort {

    override fun assignRole(member: Member, role: Role): MemberRole {
        if (memberRoleRepository.existsByMemberAndRole(member, role)) {
            throw RoleException.memberRoleAlreadyExists()
        }

        val memberRole = MemberRole(
            member = member,
            role = role
        )
        return memberRoleRepository.save(memberRole)
    }

    override fun delete(memberRole: MemberRole) {
        memberRoleRepository.delete(memberRole)
    }
}

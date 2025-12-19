package com.example.mykku.role.infrastructure.adapter

import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.role.application.port.out.RoleQueryPort
import com.example.mykku.role.application.port.out.RoleSummary
import com.example.mykku.role.domain.model.RoleId
import com.example.mykku.role.repository.MemberRoleRepository
import com.example.mykku.role.repository.RoleRepository
import org.springframework.stereotype.Component

@Component
class RoleQueryAdapter(
    private val roleRepository: RoleRepository,
    private val memberRoleRepository: MemberRoleRepository
) : RoleQueryPort {

    override fun findById(id: RoleId): RoleSummary? {
        return roleRepository.findById(id.value)
            .map { role ->
                RoleSummary(
                    id = role.id!!,
                    name = role.name,
                    description = role.description
                )
            }
            .orElse(null)
    }

    override fun findByName(name: String): RoleSummary? {
        return roleRepository.findByName(name)?.let { role ->
            RoleSummary(
                id = role.id!!,
                name = role.name,
                description = role.description
            )
        }
    }

    override fun existsById(id: RoleId): Boolean {
        return roleRepository.existsById(id.value)
    }

    override fun getMemberRoles(memberId: MemberId): List<RoleSummary> {
        return memberRoleRepository.findByMemberId(memberId.value)
            .map { memberRole ->
                RoleSummary(
                    id = memberRole.role.id!!,
                    name = memberRole.role.name,
                    description = memberRole.role.description
                )
            }
    }

    override fun hasMemberRole(memberId: MemberId, roleName: String): Boolean {
        return memberRoleRepository.existsByMemberIdAndRoleName(memberId.value, roleName)
    }
}

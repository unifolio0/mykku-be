package com.example.mykku.role.application.usecase

import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.role.adapter.output.persistence.repository.RoleJpaRepository
import com.example.mykku.role.application.dto.ChangeRepresentativeRoleCommand
import com.example.mykku.role.application.dto.MemberRoleResult
import com.example.mykku.role.application.dto.RoleResult
import com.example.mykku.role.application.port.input.ChangeRepresentativeRoleUseCase
import com.example.mykku.role.application.port.input.GetMyRolesUseCase
import com.example.mykku.role.application.port.output.MemberRoleRepository
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.exception.RoleException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoleService(
    private val memberRoleRepository: MemberRoleRepository,
    private val roleRepository: RoleRepository,
    private val memberRepository: MemberRepository,
    private val roleJpaRepository: RoleJpaRepository
) : GetMyRolesUseCase, ChangeRepresentativeRoleUseCase {

    override fun getMyRoles(memberId: String, representativeRoleId: Long?): List<MemberRoleResult> {
        val memberRolesWithRole = memberRoleRepository.findByMemberIdWithRole(memberId)

        return memberRolesWithRole.map { (memberRole, role) ->
            MemberRoleResult(
                id = memberRole.id.value,
                role = RoleResult(
                    id = role.id.value,
                    name = role.name,
                    description = role.description
                ),
                isRepresentative = representativeRoleId == role.id.value,
                earnedAt = memberRole.createdAt
            )
        }
    }

    @Transactional
    override fun changeRepresentativeRole(command: ChangeRepresentativeRoleCommand) {
        val memberRole = memberRoleRepository.findById(MemberRoleId.of(command.memberRoleId))
            ?: throw RoleException.memberRoleNotFound()

        if (memberRole.memberId != command.memberId) {
            throw RoleException.memberRoleUnauthorized()
        }

        val role = roleRepository.findById(memberRole.roleId)
            ?: throw RoleException.roleNotFound()

        val member = memberRepository.findById(command.memberId).orElseThrow {
            throw IllegalArgumentException("Member not found: ${command.memberId}")
        }

        val roleJpaEntity = roleJpaRepository.findByIdOrNull(role.id.value)
            ?: throw RoleException.roleNotFound()

        member.role = com.example.mykku.role.domain.Role(
            id = roleJpaEntity.id,
            name = roleJpaEntity.name,
            description = roleJpaEntity.description
        )
        memberRepository.save(member)
    }
}

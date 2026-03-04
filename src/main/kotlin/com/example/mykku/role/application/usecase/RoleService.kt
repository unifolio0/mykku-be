package com.example.mykku.role.application.usecase

import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.member.exception.MemberException
import com.example.mykku.role.application.dto.ChangeRepresentativeRoleCommand
import com.example.mykku.role.application.dto.MemberRoleResult
import com.example.mykku.role.application.dto.RoleResult
import com.example.mykku.role.application.port.input.ChangeRepresentativeRoleUseCase
import com.example.mykku.role.application.port.input.GetMyRolesUseCase
import com.example.mykku.role.application.port.output.MemberRoleRepository
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.exception.RoleException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoleService(
    private val memberRoleRepository: MemberRoleRepository,
    private val roleRepository: RoleRepository,
    private val memberRepository: MemberRepository
) : GetMyRolesUseCase, ChangeRepresentativeRoleUseCase {

    override fun getMyRoles(memberId: Long, representativeRoleId: Long?): List<MemberRoleResult> {
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

        val member = memberRepository.findById(MemberPk.of(command.memberId))
            ?: throw MemberException.memberNotFound()

        member.assignRole(role.id.value)
        memberRepository.save(member)
    }
}

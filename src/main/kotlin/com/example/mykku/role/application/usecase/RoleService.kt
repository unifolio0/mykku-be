package com.example.mykku.role.application.usecase

import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberPk
import com.example.mykku.member.exception.MemberException
import com.example.mykku.role.application.dto.AcquireRoleCommand
import com.example.mykku.role.application.dto.AcquireRoleResult
import com.example.mykku.role.application.dto.ChangeRepresentativeRoleCommand
import com.example.mykku.role.application.dto.MemberRoleResult
import com.example.mykku.role.application.dto.RoleResult
import com.example.mykku.role.application.port.input.AcquireRoleUseCase
import com.example.mykku.role.application.port.input.ChangeRepresentativeRoleUseCase
import com.example.mykku.role.application.port.input.GetMyRolesUseCase
import com.example.mykku.role.application.port.input.GetRolesUseCase
import com.example.mykku.role.application.port.output.MemberRoleRepository
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.entity.MemberRole
import com.example.mykku.role.domain.entity.Role
import com.example.mykku.role.domain.vo.MemberRoleId
import com.example.mykku.role.domain.vo.RoleId
import com.example.mykku.role.exception.RoleException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoleService(
    private val memberRoleRepository: MemberRoleRepository,
    private val roleRepository: RoleRepository,
    private val memberRepository: MemberRepository
) : GetMyRolesUseCase, ChangeRepresentativeRoleUseCase, AcquireRoleUseCase, GetRolesUseCase {

    override fun getMyRoles(memberId: Long, representativeRoleId: Long?): List<MemberRoleResult> {
        val memberRolesWithRole = memberRoleRepository.findByMemberIdWithRole(memberId)

        return memberRolesWithRole.map { (memberRole, role) ->
            toMemberRoleResult(memberRole, role, representativeRoleId)
        }
    }

    override fun getRoles(): List<RoleResult> {
        return roleRepository.findAll().map { toRoleResult(it) }
    }

    @Transactional
    override fun acquireRole(command: AcquireRoleCommand): AcquireRoleResult {
        val role = roleRepository.findById(RoleId.of(command.roleId))
            ?: throw RoleException.roleNotFound()
        val member = memberRepository.findById(MemberPk.of(command.memberId))
            ?: throw MemberException.memberNotFound()

        val representativeRoleId = resolveRepresentativeRoleId(member, role)
        val acquired = memberRoleRepository.saveIfAbsent(command.memberId, role.id)

        val memberRole = memberRoleRepository.findByMemberIdAndRoleId(command.memberId, role.id)
            ?: throw RoleException.roleNotFound()

        return AcquireRoleResult(acquired, toMemberRoleResult(memberRole, role, representativeRoleId))
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

        memberRepository.findById(MemberPk.of(command.memberId))
            ?: throw MemberException.memberNotFound()

        memberRepository.assignRole(MemberPk.of(command.memberId), role.id.value)
    }

    private fun resolveRepresentativeRoleId(member: Member, role: Role): Long? {
        member.roleId?.let { return it }

        if (memberRepository.assignRoleIfAbsent(member.id, role.id.value)) return role.id.value
        return memberRepository.findById(member.id)?.roleId
    }

    private fun toMemberRoleResult(
        memberRole: MemberRole,
        role: Role,
        representativeRoleId: Long?
    ): MemberRoleResult {
        return MemberRoleResult(
            id = memberRole.id.value,
            role = toRoleResult(role),
            isRepresentative = representativeRoleId == role.id.value,
            earnedAt = memberRole.createdAt
        )
    }

    private fun toRoleResult(role: Role): RoleResult {
        return RoleResult(
            id = role.id.value,
            name = role.name,
            description = role.description
        )
    }
}

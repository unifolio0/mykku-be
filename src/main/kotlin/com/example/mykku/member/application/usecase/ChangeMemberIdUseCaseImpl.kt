package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.dto.ChangeMemberIdCommand
import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.port.input.ChangeMemberIdUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.exception.MemberException
import com.example.mykku.role.application.dto.RoleResult
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.vo.RoleId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChangeMemberIdUseCaseImpl(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository
) : ChangeMemberIdUseCase {

    override fun changeMemberId(member: Member, command: ChangeMemberIdCommand): MemberProfileResult {
        if (command.memberId != member.memberId && memberRepository.existsByMemberId(command.memberId)) {
            throw MemberException.memberIdAlreadyExists()
        }

        member.changeMemberId(command.memberId)
        val savedMember = memberRepository.save(member)

        val role = savedMember.roleId?.let { roleRepository.findById(RoleId.of(it)) }
        val roleResult = role?.let { RoleResult(it.id.value, it.name, it.description) }
        return MemberProfileResult.from(savedMember, roleResult)
    }
}

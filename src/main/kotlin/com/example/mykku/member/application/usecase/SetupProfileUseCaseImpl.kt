package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.dto.SetupProfileCommand
import com.example.mykku.member.application.port.input.SetupProfileUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.exception.MemberException
import com.example.mykku.role.application.port.output.RoleRepository
import com.example.mykku.role.domain.vo.RoleId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SetupProfileUseCaseImpl(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository
) : SetupProfileUseCase {

    override fun setupProfile(member: Member, command: SetupProfileCommand): MemberProfileResult {
        if (memberRepository.existsByMemberId(command.memberId)) {
            throw MemberException.memberIdAlreadyExists()
        }

        member.setupProfile(command.memberId, command.nickname)
        val savedMember = memberRepository.save(member)

        val roleName = savedMember.roleId?.let { roleRepository.findById(RoleId.of(it))?.name }
        return MemberProfileResult.from(savedMember, roleName)
    }
}

package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.dto.UpdateProfileCommand
import com.example.mykku.member.application.port.input.UpdateMemberProfileUseCase
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
class UpdateMemberProfileUseCaseImpl(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository
) : UpdateMemberProfileUseCase {

    override fun updateProfile(member: Member, command: UpdateProfileCommand): MemberProfileResult {
        command.nickname?.let { newNickname ->
            if (newNickname != member.nickname && memberRepository.existsByNickname(newNickname)) {
                throw MemberException.nicknameAlreadyExists()
            }
        }

        member.updateProfile(command.nickname, command.profileImage)
        val savedMember = memberRepository.save(member)

        val role = savedMember.roleId?.let { roleRepository.findById(RoleId.of(it)) }
        val roleResult = role?.let { RoleResult(it.id.value, it.name, it.description) }
        return MemberProfileResult.from(savedMember, roleResult)
    }
}

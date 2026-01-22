package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.dto.UpdateProfileCommand
import com.example.mykku.member.application.port.input.UpdateMemberProfileUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.exception.MemberException
import com.example.mykku.role.tool.RoleReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateMemberProfileUseCaseImpl(
    private val memberRepository: MemberRepository,
    private val roleReader: RoleReader
) : UpdateMemberProfileUseCase {

    override fun updateProfile(member: Member, command: UpdateProfileCommand): MemberProfileResult {
        command.nickname?.let { newNickname ->
            if (newNickname != member.nickname && memberRepository.existsByNickname(newNickname)) {
                throw MemberException.nicknameAlreadyExists()
            }
        }

        member.updateProfile(command.nickname, command.profileImage)
        val savedMember = memberRepository.save(member)

        val roleName = savedMember.roleId?.let { roleReader.findById(it)?.name }
        return MemberProfileResult.from(savedMember, roleName)
    }
}

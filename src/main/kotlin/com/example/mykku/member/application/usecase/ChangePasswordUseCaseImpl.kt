package com.example.mykku.member.application.usecase

import com.example.mykku.member.application.dto.ChangePasswordCommand
import com.example.mykku.member.application.port.input.ChangePasswordUseCase
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.exception.MemberException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChangePasswordUseCaseImpl(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) : ChangePasswordUseCase {

    override fun changePassword(member: Member, command: ChangePasswordCommand) {
        if (member.password == null || !passwordEncoder.matches(command.currentPassword, member.password)) {
            throw MemberException.invalidCurrentPassword()
        }

        val encodedNewPassword = passwordEncoder.encode(command.newPassword)
        member.changePassword(encodedNewPassword)
        memberRepository.save(member)
    }
}

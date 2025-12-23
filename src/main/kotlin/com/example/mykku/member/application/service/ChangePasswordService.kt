package com.example.mykku.member.application.service

import com.example.mykku.member.application.port.`in`.ChangePasswordCommand
import com.example.mykku.member.application.port.`in`.ChangePasswordUseCase
import com.example.mykku.member.application.port.out.MemberRepositoryPort
import com.example.mykku.member.application.port.out.PasswordEncoderPort
import com.example.mykku.member.domain.model.Password
import com.example.mykku.member.exception.MemberException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChangePasswordService(
    private val memberRepositoryPort: MemberRepositoryPort,
    private val passwordEncoderPort: PasswordEncoderPort
) : ChangePasswordUseCase {

    override fun execute(command: ChangePasswordCommand) {
        val member = memberRepositoryPort.findById(command.memberId)
            ?: throw MemberException.memberNotFound()

        val currentEncodedPassword = member.password?.value
            ?: throw MemberException.invalidCurrentPassword()

        if (!passwordEncoderPort.matches(command.currentPassword, currentEncodedPassword)) {
            throw MemberException.invalidCurrentPassword()
        }

        val encodedNewPassword = passwordEncoderPort.encode(command.newPassword)
        member.changePassword(Password(encodedNewPassword))

        memberRepositoryPort.save(member)
    }
}

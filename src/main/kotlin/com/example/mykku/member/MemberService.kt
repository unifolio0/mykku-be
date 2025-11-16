package com.example.mykku.member

import com.example.mykku.member.domain.Member
import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.tool.MemberWriter
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberWriter: MemberWriter,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun changePassword(member: Member, currentPassword: String, newPassword: String) {
        if (member.password == null || !passwordEncoder.matches(currentPassword, member.password)) {
            throw MemberException.invalidCurrentPassword()
        }

        val encodedNewPassword = passwordEncoder.encode(newPassword)
        member.password = encodedNewPassword
        memberWriter.save(member)
    }
}

package com.example.mykku.member

import com.example.mykku.member.domain.Member
import com.example.mykku.member.dto.MemberProfileResponse
import com.example.mykku.member.dto.UpdateProfileRequest
import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.MemberWriter
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberReader: MemberReader,
    private val memberWriter: MemberWriter,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional(readOnly = true)
    fun getMyProfile(member: Member): MemberProfileResponse {
        return MemberProfileResponse.from(member)
    }

    @Transactional
    fun updateProfile(member: Member, request: UpdateProfileRequest): MemberProfileResponse {
        request.nickname?.let { newNickname ->
            if (newNickname != member.nickname && memberReader.existsByNickname(newNickname)) {
                throw MemberException.nicknameAlreadyExists()
            }
        }

        member.updateProfile(request.nickname, request.profileImage)
        memberWriter.save(member)

        return MemberProfileResponse.from(member)
    }

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

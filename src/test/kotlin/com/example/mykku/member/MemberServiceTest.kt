package com.example.mykku.member

import com.example.mykku.BaseServiceTest
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.tool.MemberWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import com.example.mykku.role.domain.Role

class MemberServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var memberWriter: MemberWriter

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var memberService: MemberService

    @Test
    fun `비밀번호 변경 성공`() {
        val currentPassword = "oldPassword123!"
        val newPassword = "newPassword123!"
        val encodedCurrentPassword = "encodedOldPassword"
        val encodedNewPassword = "encodedNewPassword"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = "test@example.com",
            password = encodedCurrentPassword
        )

        whenever(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true)
        whenever(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword)

        memberService.changePassword(member, currentPassword, newPassword)

        assertEquals(encodedNewPassword, member.password)
        verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword)
        verify(passwordEncoder).encode(newPassword)
        verify(memberWriter).save(member)
    }

    @Test
    fun `현재 비밀번호가 일치하지 않으면 예외 발생`() {
        val currentPassword = "wrongPassword"
        val newPassword = "newPassword123!"
        val encodedCurrentPassword = "encodedOldPassword"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = "test@example.com",
            password = encodedCurrentPassword
        )

        whenever(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(false)

        val exception = assertThrows<MemberException> {
            memberService.changePassword(member, currentPassword, newPassword)
        }

        assertEquals(MemberErrorCode.INVALID_CURRENT_PASSWORD, exception.errorCode)
    }

    @Test
    fun `비밀번호가 null인 경우 예외 발생`() {
        val currentPassword = "oldPassword123!"
        val newPassword = "newPassword123!"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = "test@example.com",
            password = null
        )

        val exception = assertThrows<MemberException> {
            memberService.changePassword(member, currentPassword, newPassword)
        }

        assertEquals(MemberErrorCode.INVALID_CURRENT_PASSWORD, exception.errorCode)
    }
}

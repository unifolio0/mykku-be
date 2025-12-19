package com.example.mykku.member.application.service

import com.example.mykku.member.application.port.`in`.ChangePasswordCommand
import com.example.mykku.member.application.port.out.MemberRepositoryPort
import com.example.mykku.member.application.port.out.PasswordEncoderPort
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.domain.model.*
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ChangePasswordServiceTest {

    @Mock
    private lateinit var memberRepositoryPort: MemberRepositoryPort

    @Mock
    private lateinit var passwordEncoderPort: PasswordEncoderPort

    private lateinit var changePasswordService: ChangePasswordService

    @BeforeEach
    fun setUp() {
        changePasswordService = ChangePasswordService(memberRepositoryPort, passwordEncoderPort)
    }

    @Test
    fun `비밀번호 변경 성공`() {
        val memberId = MemberId("user-123")
        val member = createTestMember(memberId, "encodedCurrentPassword")
        val command = ChangePasswordCommand(memberId, "currentPassword", "newPassword")

        whenever(memberRepositoryPort.findById(memberId)).thenReturn(member)
        whenever(passwordEncoderPort.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true)
        whenever(passwordEncoderPort.encode("newPassword")).thenReturn("encodedNewPassword")
        whenever(memberRepositoryPort.save(any())).thenAnswer { it.arguments[0] }

        changePasswordService.execute(command)

        verify(memberRepositoryPort).save(argThat<MemberDomain> {
            password?.value == "encodedNewPassword"
        })
    }

    @Test
    fun `회원을 찾을 수 없으면 예외 발생`() {
        val memberId = MemberId("non-existent")
        val command = ChangePasswordCommand(memberId, "current", "new")

        whenever(memberRepositoryPort.findById(memberId)).thenReturn(null)

        assertThatThrownBy { changePasswordService.execute(command) }
            .isInstanceOf(MemberException::class.java)
            .extracting("errorCode")
            .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
    }

    @Test
    fun `현재 비밀번호가 일치하지 않으면 예외 발생`() {
        val memberId = MemberId("user-123")
        val member = createTestMember(memberId, "encodedPassword")
        val command = ChangePasswordCommand(memberId, "wrongPassword", "newPassword")

        whenever(memberRepositoryPort.findById(memberId)).thenReturn(member)
        whenever(passwordEncoderPort.matches("wrongPassword", "encodedPassword")).thenReturn(false)

        assertThatThrownBy { changePasswordService.execute(command) }
            .isInstanceOf(MemberException::class.java)
            .extracting("errorCode")
            .isEqualTo(MemberErrorCode.INVALID_CURRENT_PASSWORD)
    }

    private fun createTestMember(id: MemberId, password: String): MemberDomain {
        return MemberDomain.reconstitute(
            id = id,
            nickname = Nickname("테스트"),
            email = Email("test@example.com"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            password = Password(password),
            emailVerified = true,
            followerCount = 0,
            followingCount = 0,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}

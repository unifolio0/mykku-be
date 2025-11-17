package com.example.mykku.email

import com.example.mykku.BaseServiceTest
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.exception.EmailAuthErrorCode
import com.example.mykku.email.exception.EmailAuthException
import com.example.mykku.email.tool.EmailSender
import com.example.mykku.email.tool.RedisVerificationCodeManager
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.MemberWriter
import com.example.mykku.role.domain.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

class EmailAuthServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var emailSender: EmailSender

    @Mock
    private lateinit var redisVerificationCodeManager: RedisVerificationCodeManager

    @Mock
    private lateinit var memberReader: MemberReader

    @Mock
    private lateinit var memberWriter: MemberWriter

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @InjectMocks
    private lateinit var emailAuthService: EmailAuthService

    @Test
    fun `회원가입용 인증 코드 발송 성공`() {
        val email = "test@example.com"
        val code = "123456"

        whenever(memberReader.existsByEmail(email)).thenReturn(false)
        whenever(redisVerificationCodeManager.saveVerificationCode(email, VerificationPurpose.SIGNUP.name))
            .thenReturn(code)

        emailAuthService.sendVerificationCode(email, VerificationPurpose.SIGNUP)

        verify(memberReader).existsByEmail(email)
        verify(redisVerificationCodeManager).saveVerificationCode(email, VerificationPurpose.SIGNUP.name)
        verify(emailSender).sendVerificationCode(email, code, VerificationPurpose.SIGNUP)
    }

    @Test
    fun `이미 존재하는 이메일로 회원가입 인증 코드 발송 시 예외 발생`() {
        val email = "existing@example.com"

        whenever(memberReader.existsByEmail(email)).thenReturn(true)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.sendVerificationCode(email, VerificationPurpose.SIGNUP)
        }

        assertEquals(EmailAuthErrorCode.EMAIL_ALREADY_EXISTS, exception.errorCode)
    }

    @Test
    fun `비밀번호 재설정용 인증 코드 발송 성공`() {
        val email = "test@example.com"
        val code = "123456"

        whenever(redisVerificationCodeManager.saveVerificationCode(email, VerificationPurpose.PASSWORD_RESET.name))
            .thenReturn(code)

        emailAuthService.sendVerificationCode(email, VerificationPurpose.PASSWORD_RESET)

        verify(redisVerificationCodeManager).saveVerificationCode(email, VerificationPurpose.PASSWORD_RESET.name)
        verify(emailSender).sendVerificationCode(email, code, VerificationPurpose.PASSWORD_RESET)
    }

    @Test
    fun `인증 코드 검증 성공`() {
        val email = "test@example.com"
        val code = "123456"

        whenever(redisVerificationCodeManager.getVerificationCode(email, VerificationPurpose.SIGNUP.name))
            .thenReturn(code)

        emailAuthService.verifyCode(email, code, VerificationPurpose.SIGNUP)

        verify(redisVerificationCodeManager).getVerificationCode(email, VerificationPurpose.SIGNUP.name)
        verify(redisVerificationCodeManager).deleteVerificationCode(email, VerificationPurpose.SIGNUP.name)
    }

    @Test
    fun `만료된 인증 코드 검증 시 예외 발생`() {
        val email = "test@example.com"
        val code = "123456"

        whenever(redisVerificationCodeManager.getVerificationCode(email, VerificationPurpose.SIGNUP.name))
            .thenReturn(null)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.verifyCode(email, code, VerificationPurpose.SIGNUP)
        }

        assertEquals(EmailAuthErrorCode.VERIFICATION_CODE_EXPIRED, exception.errorCode)
    }

    @Test
    fun `잘못된 인증 코드 검증 시 예외 발생`() {
        val email = "test@example.com"
        val correctCode = "123456"
        val wrongCode = "654321"

        whenever(redisVerificationCodeManager.getVerificationCode(email, VerificationPurpose.SIGNUP.name))
            .thenReturn(correctCode)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.verifyCode(email, wrongCode, VerificationPurpose.SIGNUP)
        }

        assertEquals(EmailAuthErrorCode.INVALID_VERIFICATION_CODE, exception.errorCode)
    }

    @Test
    fun `회원가입 성공`() {
        val email = "test@example.com"
        val password = "password123!"
        val nickname = "테스트"
        val encodedPassword = "encodedPassword"
        val role = Role(name = "일반 덕후", description = "테스트용 칭호")
        val member = Member.createEmailMember(
            id = "memberId",
            email = email,
            password = encodedPassword,
            nickname = nickname,
            defaultRole = role
        )
        val loginResponse = LoginResponse(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            accessTokenExpiresIn = 86400000,
            refreshTokenExpiresIn = 1209600000,
            member = MemberInfo(
                id = member.id,
                email = email,
                nickname = nickname,
                profileImage = ""
            ),
            isExistingUser = false
        )

        whenever(memberReader.existsByEmail(email)).thenReturn(false)
        whenever(passwordEncoder.encode(password)).thenReturn(encodedPassword)
        whenever(memberWriter.save(any())).thenReturn(member)
        whenever(jwtTokenProvider.createLoginResponse(any(), any(), any())).thenReturn(loginResponse)

        val result = emailAuthService.signup(email, password, nickname)

        assertNotNull(result)
        assertEquals(loginResponse.accessToken, result.accessToken)
        verify(memberReader).existsByEmail(email)
        verify(passwordEncoder).encode(password)
        verify(memberWriter).save(any())
        verify(jwtTokenProvider).createLoginResponse(any(), any(), any())
    }

    @Test
    fun `이미 존재하는 이메일로 회원가입 시 예외 발생`() {
        val email = "existing@example.com"
        val password = "password123!"
        val nickname = "테스트"

        whenever(memberReader.existsByEmail(email)).thenReturn(true)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.signup(email, password, nickname)
        }

        assertEquals(EmailAuthErrorCode.EMAIL_ALREADY_EXISTS, exception.errorCode)
    }

    @Test
    fun `로그인 성공`() {
        val email = "test@example.com"
        val password = "password123!"
        val encodedPassword = "encodedPassword"
        val role = Role(name = "일반 덕후", description = "테스트용 칭호")
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = role,
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = email,
            password = encodedPassword
        )
        val loginResponse = LoginResponse(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            accessTokenExpiresIn = 86400000,
            refreshTokenExpiresIn = 1209600000,
            member = MemberInfo(
                id = member.id,
                email = email,
                nickname = member.nickname,
                profileImage = member.profileImage
            ),
            isExistingUser = true
        )

        whenever(memberReader.findByEmail(email)).thenReturn(member)
        whenever(passwordEncoder.matches(password, encodedPassword)).thenReturn(true)
        whenever(jwtTokenProvider.createLoginResponse(member, email, true)).thenReturn(loginResponse)

        val result = emailAuthService.login(email, password)

        assertNotNull(result)
        assertEquals(loginResponse.accessToken, result.accessToken)
        verify(memberReader).findByEmail(email)
        verify(passwordEncoder).matches(password, encodedPassword)
        verify(jwtTokenProvider).createLoginResponse(member, email, true)
    }

    @Test
    fun `존재하지 않는 이메일로 로그인 시 예외 발생`() {
        val email = "nonexistent@example.com"
        val password = "password123!"

        whenever(memberReader.findByEmail(email)).thenReturn(null)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.login(email, password)
        }

        assertEquals(EmailAuthErrorCode.INVALID_EMAIL_OR_PASSWORD, exception.errorCode)
    }

    @Test
    fun `잘못된 비밀번호로 로그인 시 예외 발생`() {
        val email = "test@example.com"
        val password = "wrongPassword"
        val encodedPassword = "encodedPassword"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = email,
            password = encodedPassword
        )

        whenever(memberReader.findByEmail(email)).thenReturn(member)
        whenever(passwordEncoder.matches(password, encodedPassword)).thenReturn(false)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.login(email, password)
        }

        assertEquals(EmailAuthErrorCode.INVALID_EMAIL_OR_PASSWORD, exception.errorCode)
    }

    @Test
    fun `비밀번호 재설정 성공`() {
        val email = "test@example.com"
        val code = "123456"
        val newPassword = "newPassword123!"
        val encodedPassword = "encodedNewPassword"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = email,
            password = "oldPassword"
        )

        whenever(redisVerificationCodeManager.getVerificationCode(email, VerificationPurpose.PASSWORD_RESET.name))
            .thenReturn(code)
        whenever(memberReader.findByEmail(email)).thenReturn(member)
        whenever(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword)

        emailAuthService.resetPassword(email, code, newPassword)

        assertEquals(encodedPassword, member.password)
        verify(redisVerificationCodeManager).getVerificationCode(email, VerificationPurpose.PASSWORD_RESET.name)
        verify(memberReader).findByEmail(email)
        verify(passwordEncoder).encode(newPassword)
        verify(memberWriter).save(member)
        verify(redisVerificationCodeManager).deleteVerificationCode(email, VerificationPurpose.PASSWORD_RESET.name)
    }

    @Test
    fun `만료된 인증 코드로 비밀번호 재설정 시 예외 발생`() {
        val email = "test@example.com"
        val code = "123456"
        val newPassword = "newPassword123!"

        whenever(redisVerificationCodeManager.getVerificationCode(email, VerificationPurpose.PASSWORD_RESET.name))
            .thenReturn(null)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.resetPassword(email, code, newPassword)
        }

        assertEquals(EmailAuthErrorCode.VERIFICATION_CODE_EXPIRED, exception.errorCode)
    }

    @Test
    fun `잘못된 인증 코드로 비밀번호 재설정 시 예외 발생`() {
        val email = "test@example.com"
        val correctCode = "123456"
        val wrongCode = "654321"
        val newPassword = "newPassword123!"

        whenever(redisVerificationCodeManager.getVerificationCode(email, VerificationPurpose.PASSWORD_RESET.name))
            .thenReturn(correctCode)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.resetPassword(email, wrongCode, newPassword)
        }

        assertEquals(EmailAuthErrorCode.INVALID_VERIFICATION_CODE, exception.errorCode)
    }

    @Test
    fun `임시 비밀번호 발송 성공`() {
        val email = "test@example.com"
        val temporaryPassword = "aB3!k9@mP2"
        val encodedPassword = "encodedTemporaryPassword"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = email,
            password = "oldPassword"
        )

        whenever(memberReader.findByEmail(email)).thenReturn(member)
        whenever(passwordEncoder.encode(any())).thenReturn(encodedPassword)

        emailAuthService.sendTemporaryPassword(email)

        assertEquals(encodedPassword, member.password)
        verify(memberReader).findByEmail(email)
        verify(passwordEncoder).encode(any())
        verify(memberWriter).save(member)
        verify(emailSender).sendTemporaryPassword(any(), any())
    }

    @Test
    fun `존재하지 않는 이메일로 임시 비밀번호 발송 시 예외 발생`() {
        val email = "nonexistent@example.com"

        whenever(memberReader.findByEmail(email)).thenReturn(null)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.sendTemporaryPassword(email)
        }

        assertEquals(EmailAuthErrorCode.INVALID_EMAIL_OR_PASSWORD, exception.errorCode)
    }

    @Test
    fun `이메일 발송 실패 시 예외 발생`() {
        val email = "test@example.com"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = email,
            password = "oldPassword"
        )

        whenever(memberReader.findByEmail(email)).thenReturn(member)
        whenever(passwordEncoder.encode(any())).thenReturn("encodedPassword")
        whenever(emailSender.sendTemporaryPassword(any(), any())).thenThrow(RuntimeException("Email send failed"))

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.sendTemporaryPassword(email)
        }

        assertEquals(EmailAuthErrorCode.EMAIL_SEND_FAILED, exception.errorCode)
    }
}

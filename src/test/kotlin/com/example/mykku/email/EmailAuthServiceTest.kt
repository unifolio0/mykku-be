package com.example.mykku.email

import com.example.mykku.BaseServiceTest
import com.example.mykku.auth.application.port.out.JwtTokenPort
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.dto.MemberInfo
import com.example.mykku.email.application.port.out.EmailSenderPort
import com.example.mykku.email.application.port.out.VerificationCodePort
import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.exception.EmailAuthErrorCode
import com.example.mykku.email.exception.EmailAuthException
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.application.port.out.MemberRepositoryPort
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.domain.model.Email
import com.example.mykku.member.domain.model.MemberDomain
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.member.domain.model.Nickname
import com.example.mykku.member.domain.model.Password
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
    private lateinit var emailSenderPort: EmailSenderPort

    @Mock
    private lateinit var verificationCodePort: VerificationCodePort

    @Mock
    private lateinit var memberQueryPort: MemberQueryPort

    @Mock
    private lateinit var memberRepositoryPort: MemberRepositoryPort

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtTokenPort: JwtTokenPort

    @Mock
    private lateinit var roleQueryPort: com.example.mykku.role.application.port.out.RoleQueryPort

    @Mock
    private lateinit var memberRoleRepositoryPort: com.example.mykku.role.application.port.out.MemberRoleRepositoryPort

    @InjectMocks
    private lateinit var emailAuthService: EmailAuthService

    @Test
    fun `회원가입용 인증 코드 발송 성공`() {
        val email = "test@example.com"
        val code = "123456"

        whenever(memberQueryPort.existsByEmailString(email)).thenReturn(false)
        whenever(verificationCodePort.generateCode()).thenReturn(code)

        emailAuthService.sendVerificationCode(email, VerificationPurpose.SIGNUP)

        verify(memberQueryPort).existsByEmailString(email)
        verify(verificationCodePort).generateCode()
        verify(verificationCodePort).saveCode(email, code, VerificationPurpose.SIGNUP)
        verify(emailSenderPort).sendVerificationCode(email, code, VerificationPurpose.SIGNUP)
    }

    @Test
    fun `이미 존재하는 이메일로 회원가입 인증 코드 발송 시 예외 발생`() {
        val email = "existing@example.com"

        whenever(memberQueryPort.existsByEmailString(email)).thenReturn(true)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.sendVerificationCode(email, VerificationPurpose.SIGNUP)
        }

        assertEquals(EmailAuthErrorCode.EMAIL_ALREADY_EXISTS, exception.errorCode)
    }

    @Test
    fun `비밀번호 재설정용 인증 코드 발송 성공`() {
        val email = "test@example.com"
        val code = "123456"

        whenever(verificationCodePort.generateCode()).thenReturn(code)

        emailAuthService.sendVerificationCode(email, VerificationPurpose.PASSWORD_RESET)

        verify(verificationCodePort).generateCode()
        verify(verificationCodePort).saveCode(email, code, VerificationPurpose.PASSWORD_RESET)
        verify(emailSenderPort).sendVerificationCode(email, code, VerificationPurpose.PASSWORD_RESET)
    }

    @Test
    fun `인증 코드 검증 성공`() {
        val email = "test@example.com"
        val code = "123456"

        whenever(verificationCodePort.getCode(email, VerificationPurpose.SIGNUP))
            .thenReturn(code)

        emailAuthService.verifyCode(email, code, VerificationPurpose.SIGNUP)

        verify(verificationCodePort).getCode(email, VerificationPurpose.SIGNUP)
        verify(verificationCodePort).deleteCode(email, VerificationPurpose.SIGNUP)
    }

    @Test
    fun `만료된 인증 코드 검증 시 예외 발생`() {
        val email = "test@example.com"
        val code = "123456"

        whenever(verificationCodePort.getCode(email, VerificationPurpose.SIGNUP))
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

        whenever(verificationCodePort.getCode(email, VerificationPurpose.SIGNUP))
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

        val memberDomain = MemberDomain.createEmailMember(
            id = MemberId("memberId"),
            email = Email(email),
            password = Password(encodedPassword),
            nickname = Nickname(nickname)
        )

        val member = Member.createEmailMember(
            id = "memberId",
            email = email,
            password = encodedPassword,
            nickname = nickname
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

        whenever(memberQueryPort.existsByEmailString(email)).thenReturn(false)
        whenever(passwordEncoder.encode(password)).thenReturn(encodedPassword)
        whenever(memberRepositoryPort.save(any())).thenReturn(memberDomain)
        whenever(memberQueryPort.getMemberById(MemberId("memberId"))).thenReturn(member)
        whenever(jwtTokenPort.createLoginResponse(any(), any(), any())).thenReturn(loginResponse)

        val result = emailAuthService.signup(email, password, nickname)

        assertNotNull(result)
        assertEquals(loginResponse.accessToken, result.accessToken)
        verify(memberQueryPort).existsByEmailString(email)
        verify(passwordEncoder).encode(password)
        verify(memberRepositoryPort).save(any())
        verify(jwtTokenPort).createLoginResponse(any(), any(), any())
    }

    @Test
    fun `이미 존재하는 이메일로 회원가입 시 예외 발생`() {
        val email = "existing@example.com"
        val password = "password123!"
        val nickname = "테스트"

        whenever(memberQueryPort.existsByEmailString(email)).thenReturn(true)

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

        whenever(memberQueryPort.findMemberByEmail(email)).thenReturn(member)
        whenever(passwordEncoder.matches(password, encodedPassword)).thenReturn(true)
        whenever(jwtTokenPort.createLoginResponse(member, email, true)).thenReturn(loginResponse)

        val result = emailAuthService.login(email, password)

        assertNotNull(result)
        assertEquals(loginResponse.accessToken, result.accessToken)
        verify(memberQueryPort).findMemberByEmail(email)
        verify(passwordEncoder).matches(password, encodedPassword)
        verify(jwtTokenPort).createLoginResponse(member, email, true)
    }

    @Test
    fun `존재하지 않는 이메일로 로그인 시 예외 발생`() {
        val email = "nonexistent@example.com"
        val password = "password123!"

        whenever(memberQueryPort.findMemberByEmail(email)).thenReturn(null)

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

        whenever(memberQueryPort.findMemberByEmail(email)).thenReturn(member)
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

        val memberDomain = MemberDomain.createEmailMember(
            id = MemberId("memberId"),
            email = Email(email),
            password = Password("oldPassword"),
            nickname = Nickname("테스트")
        )

        whenever(verificationCodePort.getCode(email, VerificationPurpose.PASSWORD_RESET))
            .thenReturn(code)
        whenever(memberQueryPort.findMemberByEmail(email)).thenReturn(member)
        whenever(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword)
        whenever(memberRepositoryPort.findById(MemberId("memberId"))).thenReturn(memberDomain)
        whenever(memberRepositoryPort.save(any())).thenReturn(memberDomain)

        emailAuthService.resetPassword(email, code, newPassword)

        verify(verificationCodePort).getCode(email, VerificationPurpose.PASSWORD_RESET)
        verify(memberQueryPort).findMemberByEmail(email)
        verify(passwordEncoder).encode(newPassword)
        verify(memberRepositoryPort).findById(MemberId("memberId"))
        verify(memberRepositoryPort).save(any())
        verify(verificationCodePort).deleteCode(email, VerificationPurpose.PASSWORD_RESET)
    }

    @Test
    fun `만료된 인증 코드로 비밀번호 재설정 시 예외 발생`() {
        val email = "test@example.com"
        val code = "123456"
        val newPassword = "newPassword123!"

        whenever(verificationCodePort.getCode(email, VerificationPurpose.PASSWORD_RESET))
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

        whenever(verificationCodePort.getCode(email, VerificationPurpose.PASSWORD_RESET))
            .thenReturn(correctCode)

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.resetPassword(email, wrongCode, newPassword)
        }

        assertEquals(EmailAuthErrorCode.INVALID_VERIFICATION_CODE, exception.errorCode)
    }

    @Test
    fun `임시 비밀번호 발송 성공`() {
        val email = "test@example.com"
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

        val memberDomain = MemberDomain.createEmailMember(
            id = MemberId("memberId"),
            email = Email(email),
            password = Password("oldPassword"),
            nickname = Nickname("테스트")
        )

        whenever(memberQueryPort.findMemberByEmail(email)).thenReturn(member)
        whenever(passwordEncoder.encode(any())).thenReturn(encodedPassword)
        whenever(memberRepositoryPort.findById(MemberId("memberId"))).thenReturn(memberDomain)
        whenever(memberRepositoryPort.save(any())).thenReturn(memberDomain)

        emailAuthService.sendTemporaryPassword(email)

        verify(memberQueryPort).findMemberByEmail(email)
        verify(passwordEncoder).encode(any())
        verify(memberRepositoryPort).findById(MemberId("memberId"))
        verify(memberRepositoryPort).save(any())
        verify(emailSenderPort).sendTemporaryPassword(any(), any())
    }

    @Test
    fun `존재하지 않는 이메일로 임시 비밀번호 발송 시 예외 발생`() {
        val email = "nonexistent@example.com"

        whenever(memberQueryPort.findMemberByEmail(email)).thenReturn(null)

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

        val memberDomain = MemberDomain.createEmailMember(
            id = MemberId("memberId"),
            email = Email(email),
            password = Password("oldPassword"),
            nickname = Nickname("테스트")
        )

        whenever(memberQueryPort.findMemberByEmail(email)).thenReturn(member)
        whenever(passwordEncoder.encode(any())).thenReturn("encodedPassword")
        whenever(memberRepositoryPort.findById(MemberId("memberId"))).thenReturn(memberDomain)
        whenever(memberRepositoryPort.save(any())).thenReturn(memberDomain)
        whenever(emailSenderPort.sendTemporaryPassword(any(), any())).thenThrow(RuntimeException("Email send failed"))

        val exception = assertThrows<EmailAuthException> {
            emailAuthService.sendTemporaryPassword(email)
        }

        assertEquals(EmailAuthErrorCode.EMAIL_SEND_FAILED, exception.errorCode)
    }
}

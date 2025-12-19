package com.example.mykku.email

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.exception.EmailAuthException
import com.example.mykku.email.tool.EmailSender
import com.example.mykku.email.tool.RedisVerificationCodeManager
import com.example.mykku.email.util.TemporaryPasswordGenerator
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.application.port.out.MemberRepositoryPort
import com.example.mykku.member.domain.model.Email
import com.example.mykku.member.domain.model.MemberDomain
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.member.domain.model.Nickname
import com.example.mykku.member.domain.model.Password
import com.example.mykku.role.application.port.out.MemberRoleRepositoryPort
import com.example.mykku.role.application.port.out.RoleQueryPort
import java.util.UUID
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmailAuthService(
    private val emailSender: EmailSender,
    private val redisVerificationCodeManager: RedisVerificationCodeManager,
    private val memberQueryPort: MemberQueryPort,
    private val memberRepositoryPort: MemberRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val roleQueryPort: RoleQueryPort,
    private val memberRoleRepositoryPort: MemberRoleRepositoryPort
) {

    @Transactional
    fun sendVerificationCode(email: String, purpose: VerificationPurpose) {
        if (purpose == VerificationPurpose.SIGNUP && memberQueryPort.existsByEmailString(email)) {
            throw EmailAuthException.emailAlreadyExists()
        }

        val code = redisVerificationCodeManager.saveVerificationCode(email, purpose.name)

        try {
            emailSender.sendVerificationCode(
                to = email,
                code = code,
                purpose = purpose
            )
        } catch (e: Exception) {
            throw EmailAuthException.emailSendFailed(e)
        }
    }

    @Transactional
    fun verifyCode(email: String, code: String, purpose: VerificationPurpose) {
        val savedCode = redisVerificationCodeManager.getVerificationCode(email, purpose.name)
            ?: throw EmailAuthException.verificationCodeExpired()

        if (savedCode != code) {
            throw EmailAuthException.invalidVerificationCode()
        }

        redisVerificationCodeManager.deleteVerificationCode(email, purpose.name)
    }

    @Transactional
    fun signup(email: String, password: String, nickname: String): LoginResponse {
        if (memberQueryPort.existsByEmailString(email)) {
            throw EmailAuthException.emailAlreadyExists()
        }

        val encodedPassword = passwordEncoder.encode(password)
        val memberId = UUID.randomUUID().toString()

        val memberDomain = MemberDomain.createEmailMember(
            id = MemberId(memberId),
            email = Email(email),
            password = Password(encodedPassword),
            nickname = Nickname(nickname)
        )

        val savedMember = memberRepositoryPort.save(memberDomain)

        val member = memberQueryPort.getMemberById(savedMember.id)
        return jwtTokenProvider.createLoginResponse(member, email, false)
    }

    @Transactional(readOnly = true)
    fun login(email: String, password: String): LoginResponse {
        val member = memberQueryPort.findMemberByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        if (member.password == null || !passwordEncoder.matches(password, member.password)) {
            throw EmailAuthException.invalidEmailOrPassword()
        }

        return jwtTokenProvider.createLoginResponse(member, email, true)
    }

    @Transactional
    fun resetPassword(email: String, code: String, newPassword: String) {
        verifyCode(email, code, VerificationPurpose.PASSWORD_RESET)

        val member = memberQueryPort.findMemberByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        val encodedPassword = passwordEncoder.encode(newPassword)
        member.password = encodedPassword

        val memberDomain = memberRepositoryPort.findById(MemberId(member.id))
            ?: throw EmailAuthException.invalidEmailOrPassword()
        memberDomain.changePassword(Password(encodedPassword))
        memberRepositoryPort.save(memberDomain)
    }

    @Transactional
    fun sendTemporaryPassword(email: String) {
        val member = memberQueryPort.findMemberByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        val temporaryPassword = TemporaryPasswordGenerator.generate()
        val encodedPassword = passwordEncoder.encode(temporaryPassword)

        val memberDomain = memberRepositoryPort.findById(MemberId(member.id))
            ?: throw EmailAuthException.invalidEmailOrPassword()
        memberDomain.changePassword(Password(encodedPassword))
        memberRepositoryPort.save(memberDomain)

        try {
            emailSender.sendTemporaryPassword(
                to = email,
                temporaryPassword = temporaryPassword
            )
        } catch (e: Exception) {
            throw EmailAuthException.emailSendFailed(e)
        }
    }
}

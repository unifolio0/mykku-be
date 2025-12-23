package com.example.mykku.email

import com.example.mykku.auth.application.port.out.JwtTokenPort
import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.email.application.port.out.EmailSenderPort
import com.example.mykku.email.application.port.out.VerificationCodePort
import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.exception.EmailAuthException
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
    private val emailSenderPort: EmailSenderPort,
    private val verificationCodePort: VerificationCodePort,
    private val memberQueryPort: MemberQueryPort,
    private val memberRepositoryPort: MemberRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenPort: JwtTokenPort,
    private val roleQueryPort: RoleQueryPort,
    private val memberRoleRepositoryPort: MemberRoleRepositoryPort
) {

    @Transactional
    fun sendVerificationCode(email: String, purpose: VerificationPurpose) {
        if (purpose == VerificationPurpose.SIGNUP && memberQueryPort.existsByEmailString(email)) {
            throw EmailAuthException.emailAlreadyExists()
        }

        val code = verificationCodePort.generateCode()
        verificationCodePort.saveCode(email, code, purpose)

        try {
            emailSenderPort.sendVerificationCode(
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
        val savedCode = verificationCodePort.getCode(email, purpose)
            ?: throw EmailAuthException.verificationCodeExpired()

        if (savedCode != code) {
            throw EmailAuthException.invalidVerificationCode()
        }

        verificationCodePort.deleteCode(email, purpose)
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
        return jwtTokenPort.createLoginResponse(member, email, false)
    }

    @Transactional(readOnly = true)
    fun login(email: String, password: String): LoginResponse {
        val member = memberQueryPort.findMemberByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        if (member.password == null || !passwordEncoder.matches(password, member.password)) {
            throw EmailAuthException.invalidEmailOrPassword()
        }

        return jwtTokenPort.createLoginResponse(member, email, true)
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
            emailSenderPort.sendTemporaryPassword(
                to = email,
                temporaryPassword = temporaryPassword
            )
        } catch (e: Exception) {
            throw EmailAuthException.emailSendFailed(e)
        }
    }
}

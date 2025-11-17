package com.example.mykku.email

import com.example.mykku.auth.dto.LoginResponse
import com.example.mykku.auth.tool.JwtTokenProvider
import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.exception.EmailAuthException
import com.example.mykku.email.tool.EmailSender
import com.example.mykku.email.tool.RedisVerificationCodeManager
import com.example.mykku.email.util.TemporaryPasswordGenerator
import com.example.mykku.member.domain.Member
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.MemberWriter
import com.example.mykku.role.tool.MemberRoleWriter
import com.example.mykku.role.tool.RoleReader
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class EmailAuthService(
    private val emailSender: EmailSender,
    private val redisVerificationCodeManager: RedisVerificationCodeManager,
    private val memberReader: MemberReader,
    private val memberWriter: MemberWriter,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val roleReader: RoleReader,
    private val memberRoleWriter: MemberRoleWriter
) {

    @Transactional
    fun sendVerificationCode(email: String, purpose: VerificationPurpose) {
        if (purpose == VerificationPurpose.SIGNUP && memberReader.existsByEmail(email)) {
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
        if (memberReader.existsByEmail(email)) {
            throw EmailAuthException.emailAlreadyExists()
        }

        val encodedPassword = passwordEncoder.encode(password)
        val memberId = UUID.randomUUID().toString()
        val defaultRole = roleReader.getRoleByName("신입 덕후")

        val member = Member.createEmailMember(
            id = memberId,
            email = email,
            password = encodedPassword,
            nickname = nickname,
            defaultRole = defaultRole
        )

        memberWriter.save(member)
        memberRoleWriter.assignRole(member, defaultRole)

        return jwtTokenProvider.createLoginResponse(member, email, false)
    }

    @Transactional(readOnly = true)
    fun login(email: String, password: String): LoginResponse {
        val member = memberReader.findByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        if (member.password == null || !passwordEncoder.matches(password, member.password)) {
            throw EmailAuthException.invalidEmailOrPassword()
        }

        return jwtTokenProvider.createLoginResponse(member, email, true)
    }

    @Transactional
    fun resetPassword(email: String, code: String, newPassword: String) {
        verifyCode(email, code, VerificationPurpose.PASSWORD_RESET)

        val member = memberReader.findByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        val encodedPassword = passwordEncoder.encode(newPassword)
        member.password = encodedPassword
        memberWriter.save(member)
    }

    @Transactional
    fun sendTemporaryPassword(email: String) {
        val member = memberReader.findByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        val temporaryPassword = TemporaryPasswordGenerator.generate()
        val encodedPassword = passwordEncoder.encode(temporaryPassword)

        member.password = encodedPassword
        memberWriter.save(member)

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

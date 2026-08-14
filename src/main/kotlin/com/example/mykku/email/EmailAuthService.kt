package com.example.mykku.email

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.auth.adapter.input.web.dto.LoginResponse
import com.example.mykku.auth.adapter.output.persistence.JwtTokenProviderAdapter
import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.exception.EmailAuthException
import com.example.mykku.email.tool.EmailSender
import com.example.mykku.email.tool.RedisVerificationCodeManager
import com.example.mykku.email.util.TemporaryPasswordGenerator
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.application.port.output.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmailAuthService(
    private val emailSender: EmailSender,
    private val redisVerificationCodeManager: RedisVerificationCodeManager,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProviderAdapter,
    private val activityEventPublisher: ActivityEventPublisher
) {

    @Transactional
    fun sendVerificationCode(email: String, purpose: VerificationPurpose) {
        if (purpose == VerificationPurpose.SIGNUP && memberRepository.existsByEmail(email)) {
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
    fun signup(email: String, password: String): LoginResponse {
        if (memberRepository.existsByEmail(email)) {
            throw EmailAuthException.emailAlreadyExists()
        }

        val savedMember = createEmailMember(email, password)
        activityEventPublisher.publish(ActivityEvent(savedMember.id.value, ActivityType.FIRST_LOGIN))

        val result = jwtTokenProvider.createLoginResult(savedMember, email, false)
        return LoginResponse.from(result)
    }

    private fun createEmailMember(email: String, password: String): Member {
        val member = Member.createEmailMember(
            email = email,
            password = passwordEncoder.encode(password)
        )

        return memberRepository.save(member)
    }

    @Transactional(readOnly = true)
    fun login(email: String, password: String): LoginResponse {
        val member = memberRepository.findByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        if (member.password == null || !passwordEncoder.matches(password, member.password)) {
            throw EmailAuthException.invalidEmailOrPassword()
        }

        val result = jwtTokenProvider.createLoginResult(member, email, true)
        return LoginResponse.from(result)
    }

    @Transactional
    fun resetPassword(email: String, code: String, newPassword: String) {
        verifyCode(email, code, VerificationPurpose.PASSWORD_RESET)

        val member = memberRepository.findByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        val encodedPassword = passwordEncoder.encode(newPassword)
        member.changePassword(encodedPassword)
        memberRepository.save(member)
    }

    @Transactional
    fun sendTemporaryPassword(email: String) {
        val member = memberRepository.findByEmail(email)
            ?: throw EmailAuthException.invalidEmailOrPassword()

        val temporaryPassword = TemporaryPasswordGenerator.generate()
        val encodedPassword = passwordEncoder.encode(temporaryPassword)

        member.changePassword(encodedPassword)
        memberRepository.save(member)

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

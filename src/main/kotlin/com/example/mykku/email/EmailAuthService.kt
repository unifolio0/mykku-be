package com.example.mykku.email

import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.exception.EmailAuthException
import com.example.mykku.email.tool.EmailSender
import com.example.mykku.email.tool.RedisVerificationCodeManager
import com.example.mykku.member.tool.MemberReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmailAuthService(
    private val emailSender: EmailSender,
    private val redisVerificationCodeManager: RedisVerificationCodeManager,
    private val memberReader: MemberReader
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
                purpose = purpose.name
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
}

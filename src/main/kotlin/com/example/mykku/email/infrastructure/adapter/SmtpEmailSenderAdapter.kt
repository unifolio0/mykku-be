package com.example.mykku.email.infrastructure.adapter

import com.example.mykku.email.application.port.out.EmailSenderPort
import com.example.mykku.email.domain.VerificationPurpose
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class SmtpEmailSenderAdapter(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine
) : EmailSenderPort {

    override fun sendVerificationCode(to: String, code: String, purpose: VerificationPurpose) {
        val subject = when (purpose) {
            VerificationPurpose.SIGNUP -> "[MyKKU] 회원가입 인증 코드"
            VerificationPurpose.PASSWORD_RESET -> "[MyKKU] 비밀번호 재설정 인증 코드"
        }

        val title = when (purpose) {
            VerificationPurpose.SIGNUP -> "회원가입 인증"
            VerificationPurpose.PASSWORD_RESET -> "비밀번호 재설정"
        }

        val context = Context()
        context.setVariable("title", title)
        context.setVariable("code", code)

        val content = templateEngine.process("email/verification-code", context)

        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(content, true)

        mailSender.send(message)
    }

    override fun sendTemporaryPassword(to: String, temporaryPassword: String) {
        val subject = "[MyKKU] 임시 비밀번호 발급"

        val context = Context()
        context.setVariable("temporaryPassword", temporaryPassword)

        val content = templateEngine.process("email/temporary-password", context)

        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(content, true)

        mailSender.send(message)
    }
}

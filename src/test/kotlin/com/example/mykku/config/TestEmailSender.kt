package com.example.mykku.config

import com.example.mykku.email.domain.VerificationPurpose
import com.example.mykku.email.tool.EmailSender
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestEmailSenderConfig {

    @Bean
    @Primary
    fun emailSender(): EmailSender {
        return object : EmailSender {
            override fun sendVerificationCode(to: String, code: String, purpose: VerificationPurpose) {
                // Do nothing in tests
            }
        }
    }
}

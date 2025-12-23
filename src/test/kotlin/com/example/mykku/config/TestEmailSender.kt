package com.example.mykku.config

import com.example.mykku.email.application.port.out.EmailSenderPort
import com.example.mykku.email.domain.VerificationPurpose
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestEmailSenderConfig {

    @Bean
    @Primary
    fun emailSenderPort(): EmailSenderPort {
        return object : EmailSenderPort {
            override fun sendVerificationCode(to: String, code: String, purpose: VerificationPurpose) {
                // Do nothing in tests
            }

            override fun sendTemporaryPassword(to: String, temporaryPassword: String) {
                // Do nothing in tests
            }
        }
    }
}

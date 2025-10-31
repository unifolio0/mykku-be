package com.example.mykku.config

import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.mail.javamail.JavaMailSender

@TestConfiguration
class TestEmailConfig {

    @Bean
    @Primary
    fun javaMailSender(): JavaMailSender {
        val mailSender = Mockito.mock(JavaMailSender::class.java)
        val mimeMessage = Mockito.mock(jakarta.mail.internet.MimeMessage::class.java)
        
        // Stub createMimeMessage to return a mock
        Mockito.`when`(mailSender.createMimeMessage()).thenReturn(mimeMessage)
        
        // Stub the send methods to do nothing
        Mockito.doNothing().`when`(mailSender).send(Mockito.any(org.springframework.mail.SimpleMailMessage::class.java))
        Mockito.doNothing().`when`(mailSender).send(Mockito.any(jakarta.mail.internet.MimeMessage::class.java))
        
        return mailSender
    }

    @Bean
    @Primary
    fun templateEngine(): org.thymeleaf.TemplateEngine {
        val templateEngine = Mockito.mock(org.thymeleaf.TemplateEngine::class.java)
        
        // Stub process method to return a dummy HTML string
        Mockito.`when`(templateEngine.process(Mockito.anyString(), Mockito.any(org.thymeleaf.context.Context::class.java)))
            .thenReturn("<html><body>Test Email</body></html>")
        
        return templateEngine
    }
}

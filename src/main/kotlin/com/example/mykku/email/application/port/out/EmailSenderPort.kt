package com.example.mykku.email.application.port.out

import com.example.mykku.email.domain.VerificationPurpose

interface EmailSenderPort {
    fun sendVerificationCode(to: String, code: String, purpose: VerificationPurpose)
    fun sendTemporaryPassword(to: String, temporaryPassword: String)
}

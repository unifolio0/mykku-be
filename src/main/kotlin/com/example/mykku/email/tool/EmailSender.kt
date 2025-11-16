package com.example.mykku.email.tool

import com.example.mykku.email.domain.VerificationPurpose

interface EmailSender {
    fun sendVerificationCode(to: String, code: String, purpose: VerificationPurpose)
    fun sendTemporaryPassword(to: String, temporaryPassword: String)
}

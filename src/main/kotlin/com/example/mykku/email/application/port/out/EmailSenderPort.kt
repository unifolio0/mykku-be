package com.example.mykku.email.application.port.out

interface EmailSenderPort {
    fun sendVerificationEmail(email: String, code: String)
    fun sendTemporaryPasswordEmail(email: String, temporaryPassword: String)
}

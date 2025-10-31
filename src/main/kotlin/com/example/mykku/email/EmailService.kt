package com.example.mykku.email

interface EmailService {
    fun sendVerificationCode(to: String, code: String, purpose: String)
}

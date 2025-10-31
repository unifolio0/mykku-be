package com.example.mykku.email.tool

interface EmailSender {
    fun sendVerificationCode(to: String, code: String, purpose: String)
}

package com.example.mykku.email.application.port.out

import com.example.mykku.email.domain.VerificationPurpose

interface VerificationCodePort {
    fun saveCode(email: String, code: String, purpose: VerificationPurpose)
    fun getCode(email: String, purpose: VerificationPurpose): String?
    fun deleteCode(email: String, purpose: VerificationPurpose)
    fun generateCode(): String
}

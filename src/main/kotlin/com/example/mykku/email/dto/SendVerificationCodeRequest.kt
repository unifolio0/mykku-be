package com.example.mykku.email.dto

import com.example.mykku.email.domain.VerificationPurpose
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SendVerificationCodeRequest(
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,

    @field:NotNull(message = "인증 목적은 필수입니다")
    val purpose: VerificationPurpose
)

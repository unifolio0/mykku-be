package com.example.mykku.email.dto

import com.example.mykku.email.domain.VerificationPurpose
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class VerifyCodeRequest(
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,

    @field:NotBlank(message = "인증 코드는 필수입니다")
    @field:Pattern(regexp = "^[0-9]{6}$", message = "인증 코드는 6자리 숫자여야 합니다")
    val code: String,

    @field:NotNull(message = "인증 목적은 필수입니다")
    val purpose: VerificationPurpose
)

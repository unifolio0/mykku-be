package com.example.mykku.email.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignupRequest(
    @field:NotBlank(message = "아이디는 필수입니다")
    @field:Size(max = 16, message = "아이디는 최대 16자까지 가능합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9]+$",
        message = "아이디는 영문과 숫자만 사용 가능합니다"
    )
    val memberId: String,

    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
        message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다"
    )
    val password: String,

    @field:NotBlank(message = "닉네임은 필수입니다")
    @field:Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다")
    @field:Pattern(
        regexp = "^[가-힣a-zA-Z0-9\\s]+$",
        message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다"
    )
    val nickname: String
)

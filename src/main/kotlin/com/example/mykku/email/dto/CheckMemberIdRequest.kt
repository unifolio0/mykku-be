package com.example.mykku.email.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CheckMemberIdRequest(
    @field:NotBlank(message = "아이디는 필수입니다")
    @field:Size(max = 16, message = "아이디는 최대 16자까지 가능합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9]+$",
        message = "아이디는 영문과 숫자만 사용 가능합니다"
    )
    val memberId: String
)

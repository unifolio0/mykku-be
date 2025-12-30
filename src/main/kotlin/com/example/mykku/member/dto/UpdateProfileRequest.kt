package com.example.mykku.member.dto

import jakarta.validation.constraints.Size

data class UpdateProfileRequest(
    @field:Size(max = 10, message = "닉네임은 10자 이하여야 합니다")
    val nickname: String?,

    val profileImage: String?
)

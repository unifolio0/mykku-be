package com.example.mykku.contest.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateAcceptanceSpeechRequest(
    @field:NotBlank(message = "수상 소감은 필수입니다")
    @field:Size(max = 1000, message = "수상 소감은 1000자 이하여야 합니다")
    val acceptanceSpeech: String
)

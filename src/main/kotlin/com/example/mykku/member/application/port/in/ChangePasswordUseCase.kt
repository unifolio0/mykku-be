package com.example.mykku.member.application.port.`in`

import com.example.mykku.member.domain.model.MemberId

interface ChangePasswordUseCase {
    fun execute(command: ChangePasswordCommand)
}

data class ChangePasswordCommand(
    val memberId: MemberId,
    val currentPassword: String,
    val newPassword: String
)
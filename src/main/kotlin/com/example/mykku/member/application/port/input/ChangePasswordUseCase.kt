package com.example.mykku.member.application.port.input

import com.example.mykku.member.application.dto.ChangePasswordCommand
import com.example.mykku.member.domain.entity.Member

interface ChangePasswordUseCase {
    fun changePassword(member: Member, command: ChangePasswordCommand)
}

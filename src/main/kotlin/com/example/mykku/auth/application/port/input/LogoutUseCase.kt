package com.example.mykku.auth.application.port.input

import com.example.mykku.auth.application.dto.LogoutCommand

interface LogoutUseCase {
    fun logout(command: LogoutCommand)
}

package com.example.mykku.auth.application.port.input

import com.example.mykku.auth.application.dto.LoginResult
import com.example.mykku.auth.application.dto.MobileLoginCommand

interface MobileLoginUseCase {
    fun login(command: MobileLoginCommand): LoginResult
}

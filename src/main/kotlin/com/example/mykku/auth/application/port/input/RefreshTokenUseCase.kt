package com.example.mykku.auth.application.port.input

import com.example.mykku.auth.application.dto.RefreshTokenCommand
import com.example.mykku.auth.application.dto.RefreshTokenResult

interface RefreshTokenUseCase {
    fun refresh(command: RefreshTokenCommand): RefreshTokenResult
}

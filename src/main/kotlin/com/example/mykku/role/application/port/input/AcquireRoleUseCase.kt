package com.example.mykku.role.application.port.input

import com.example.mykku.role.application.dto.AcquireRoleCommand
import com.example.mykku.role.application.dto.AcquireRoleResult

interface AcquireRoleUseCase {
    fun acquireRole(command: AcquireRoleCommand): AcquireRoleResult
}

package com.example.mykku.role.application.port.input

import com.example.mykku.role.application.dto.RoleResult

interface GetRolesUseCase {
    fun getRoles(): List<RoleResult>
}

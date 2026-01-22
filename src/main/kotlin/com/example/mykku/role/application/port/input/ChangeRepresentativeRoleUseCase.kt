package com.example.mykku.role.application.port.input

import com.example.mykku.role.application.dto.ChangeRepresentativeRoleCommand

interface ChangeRepresentativeRoleUseCase {
    fun changeRepresentativeRole(command: ChangeRepresentativeRoleCommand)
}

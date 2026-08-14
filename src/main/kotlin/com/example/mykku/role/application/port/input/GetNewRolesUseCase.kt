package com.example.mykku.role.application.port.input

import com.example.mykku.role.application.dto.MemberRoleResult

interface GetNewRolesUseCase {
    fun getNewRoles(memberId: Long, representativeRoleId: Long?): List<MemberRoleResult>
}

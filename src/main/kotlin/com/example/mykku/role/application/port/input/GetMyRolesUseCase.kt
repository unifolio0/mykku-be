package com.example.mykku.role.application.port.input

import com.example.mykku.role.application.dto.MemberRoleResult

interface GetMyRolesUseCase {
    fun getMyRoles(memberId: String, representativeRoleId: Long?): List<MemberRoleResult>
}

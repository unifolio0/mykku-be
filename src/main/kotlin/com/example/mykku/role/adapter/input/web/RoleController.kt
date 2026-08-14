package com.example.mykku.role.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.role.application.dto.ChangeRepresentativeRoleCommand
import com.example.mykku.role.application.port.input.ChangeRepresentativeRoleUseCase
import com.example.mykku.role.application.port.input.GetMyRolesUseCase
import com.example.mykku.role.application.port.input.GetNewRolesUseCase
import com.example.mykku.role.application.port.input.GetRolesUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/roles")
class RoleController(
    private val getMyRolesUseCase: GetMyRolesUseCase,
    private val changeRepresentativeRoleUseCase: ChangeRepresentativeRoleUseCase,
    private val getRolesUseCase: GetRolesUseCase,
    private val getNewRolesUseCase: GetNewRolesUseCase
) {
    @GetMapping
    fun getRoles(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<RoleResponse>>> {
        val results = getRolesUseCase.getRoles()
        return ResponseEntity.ok(
            ApiResponse(
                message = "칭호 목록 조회 성공",
                data = results.map { RoleResponse.from(it) }
            )
        )
    }

    @GetMapping("/me")
    fun getMyRoles(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<MemberRoleResponse>>> {
        val results = getMyRolesUseCase.getMyRoles(member.id.value, member.roleId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "내 칭호 목록 조회 성공",
                data = results.map { MemberRoleResponse.from(it) }
            )
        )
    }

    @GetMapping("/me/new")
    fun getNewRoles(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<MemberRoleResponse>>> {
        val results = getNewRolesUseCase.getNewRoles(member.id.value, member.roleId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "새로 획득한 칭호 조회 성공",
                data = results.map { MemberRoleResponse.from(it) }
            )
        )
    }

    @PatchMapping("/{memberRoleId}/representative")
    fun changeRepresentativeRole(
        @PathVariable memberRoleId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = ChangeRepresentativeRoleCommand(
            memberId = member.id.value,
            memberRoleId = memberRoleId
        )
        changeRepresentativeRoleUseCase.changeRepresentativeRole(command)
        return ResponseEntity.ok(
            ApiResponse(
                message = "대표 칭호 변경 성공",
                data = Unit
            )
        )
    }
}

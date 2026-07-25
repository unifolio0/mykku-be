package com.example.mykku.role.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.role.application.dto.ChangeRepresentativeRoleCommand
import com.example.mykku.role.application.port.input.AcquireRoleUseCase
import com.example.mykku.role.application.port.input.ChangeRepresentativeRoleUseCase
import com.example.mykku.role.application.port.input.GetMyRolesUseCase
import com.example.mykku.role.application.port.input.GetRolesUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/roles")
class RoleController(
    private val getMyRolesUseCase: GetMyRolesUseCase,
    private val changeRepresentativeRoleUseCase: ChangeRepresentativeRoleUseCase,
    private val acquireRoleUseCase: AcquireRoleUseCase,
    private val getRolesUseCase: GetRolesUseCase
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

    @PostMapping("/acquire")
    fun acquireRole(
        @CurrentMember member: Member,
        @Valid @RequestBody request: AcquireRoleRequest
    ): ResponseEntity<ApiResponse<AcquireRoleResponse>> {
        val result = acquireRoleUseCase.acquireRole(request.toCommand(member))
        return ResponseEntity.ok(
            ApiResponse(
                message = if (result.acquired) "칭호 획득 성공" else "이미 보유한 칭호입니다",
                data = AcquireRoleResponse.from(result)
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

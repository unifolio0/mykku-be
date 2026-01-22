package com.example.mykku.role.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.Member
import com.example.mykku.role.application.dto.ChangeRepresentativeRoleCommand
import com.example.mykku.role.application.port.input.ChangeRepresentativeRoleUseCase
import com.example.mykku.role.application.port.input.GetMyRolesUseCase
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
    private val changeRepresentativeRoleUseCase: ChangeRepresentativeRoleUseCase
) {
    @GetMapping("/me")
    fun getMyRoles(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<MemberRoleResponse>>> {
        val results = getMyRolesUseCase.getMyRoles(member.id, member.role?.id)
        val responses = results.map { result ->
            MemberRoleResponse(
                id = result.id,
                role = RoleResponse(
                    id = result.role.id,
                    name = result.role.name,
                    description = result.role.description
                ),
                isRepresentative = result.isRepresentative,
                earnedAt = result.earnedAt
            )
        }
        return ResponseEntity.ok(
            ApiResponse(
                message = "내 칭호 목록 조회 성공",
                data = responses
            )
        )
    }

    @PatchMapping("/{memberRoleId}/representative")
    fun changeRepresentativeRole(
        @PathVariable memberRoleId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = ChangeRepresentativeRoleCommand(
            memberId = member.id,
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

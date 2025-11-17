package com.example.mykku.role

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.Member
import com.example.mykku.role.dto.MemberRoleResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/roles")
class RoleController(
    private val roleService: RoleService
) {
    @GetMapping("/me")
    fun getMyRoles(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<MemberRoleResponse>>> {
        val roles = roleService.getMyRoles(member)
        return ResponseEntity.ok(
            ApiResponse(
                message = "내 칭호 목록 조회 성공",
                data = roles
            )
        )
    }

    @PatchMapping("/{memberRoleId}/representative")
    fun changeRepresentativeRole(
        @PathVariable memberRoleId: Long,
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<Unit>> {
        roleService.changeRepresentativeRole(member, memberRoleId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "대표 칭호 변경 성공",
                data = Unit
            )
        )
    }
}

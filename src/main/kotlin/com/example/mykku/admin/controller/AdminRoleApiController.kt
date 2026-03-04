package com.example.mykku.admin.controller

import com.example.mykku.admin.service.AdminRoleService
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.role.adapter.input.web.CreateRoleRequest
import com.example.mykku.role.adapter.input.web.MemberRoleResponse
import com.example.mykku.role.adapter.input.web.RoleResponse
import com.example.mykku.role.adapter.input.web.UpdateRoleRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/api/v1/roles")
class AdminRoleApiController(
    private val adminRoleService: AdminRoleService
) {
    @GetMapping
    fun getAllRoles(): ResponseEntity<ApiResponse<List<RoleResponse>>> {
        val roles = adminRoleService.getAllRoles()
        return ResponseEntity.ok(
            ApiResponse(
                message = "칭호 목록 조회 성공",
                data = roles
            )
        )
    }

    @PostMapping
    fun createRole(
        @RequestBody @Valid request: CreateRoleRequest
    ): ResponseEntity<ApiResponse<RoleResponse>> {
        val role = adminRoleService.createRole(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(
                message = "칭호 생성 성공",
                data = role
            )
        )
    }

    @PutMapping("/{roleId}")
    fun updateRole(
        @PathVariable roleId: Long,
        @RequestBody @Valid request: UpdateRoleRequest
    ): ResponseEntity<ApiResponse<RoleResponse>> {
        val role = adminRoleService.updateRole(roleId, request)
        return ResponseEntity.ok(
            ApiResponse(
                message = "칭호 수정 성공",
                data = role
            )
        )
    }

    @DeleteMapping("/{roleId}")
    fun deleteRole(
        @PathVariable roleId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        adminRoleService.deleteRole(roleId)
        return ResponseEntity.ok(
            ApiResponse(
                message = "칭호 삭제 성공",
                data = Unit
            )
        )
    }

    @PostMapping("/{roleId}/members/{memberId}")
    fun assignRoleToMember(
        @PathVariable roleId: Long,
        @PathVariable memberId: Long
    ): ResponseEntity<ApiResponse<MemberRoleResponse>> {
        val memberRole = adminRoleService.assignRoleToMember(roleId, memberId)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(
                message = "칭호 부여 성공",
                data = memberRole
            )
        )
    }
}

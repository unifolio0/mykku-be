package com.example.mykku.member.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.adapter.input.web.dto.ChangePasswordRequest
import com.example.mykku.member.adapter.input.web.dto.MemberProfileResponse
import com.example.mykku.member.adapter.input.web.dto.UpdateProfileRequest
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.application.port.input.ChangePasswordUseCase
import com.example.mykku.member.application.port.input.GetMemberProfileUseCase
import com.example.mykku.member.application.port.input.UpdateMemberProfileUseCase
import com.example.mykku.role.tool.RoleReader
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val getMemberProfileUseCase: GetMemberProfileUseCase,
    private val updateMemberProfileUseCase: UpdateMemberProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val roleReader: RoleReader
) {

    @GetMapping("/me")
    fun getMyProfile(
        @CurrentMember memberEntity: MemberJpaEntity
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        val member = memberEntity.toDomain()
        val result = getMemberProfileUseCase.getMyProfile(member)
        val roleName = result.roleId?.let { roleReader.findById(it)?.name }
        val response = MemberProfileResponse.from(result, roleName)
        return ResponseEntity.ok(ApiResponse("프로필 조회 성공", response))
    }

    @PatchMapping("/me")
    fun updateProfile(
        @CurrentMember memberEntity: MemberJpaEntity,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        val member = memberEntity.toDomain()
        val result = updateMemberProfileUseCase.updateProfile(member, request.toCommand())
        val roleName = result.roleId?.let { roleReader.findById(it)?.name }
        val response = MemberProfileResponse.from(result, roleName)
        return ResponseEntity.ok(ApiResponse("프로필이 수정되었습니다", response))
    }

    @PutMapping("/password")
    fun changePassword(
        @CurrentMember memberEntity: MemberJpaEntity,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        val member = memberEntity.toDomain()
        changePasswordUseCase.changePassword(member, request.toCommand())
        return ResponseEntity.ok(ApiResponse("비밀번호가 변경되었습니다", Unit))
    }
}

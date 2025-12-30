package com.example.mykku.member

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.Member
import com.example.mykku.member.dto.ChangePasswordRequest
import com.example.mykku.member.dto.MemberProfileResponse
import com.example.mykku.member.dto.UpdateProfileRequest
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
    private val memberService: MemberService
) {

    @GetMapping("/me")
    fun getMyProfile(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        val response = memberService.getMyProfile(member)
        return ResponseEntity.ok(ApiResponse("프로필 조회 성공", response))
    }

    @PatchMapping("/me")
    fun updateProfile(
        @CurrentMember member: Member,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        val response = memberService.updateProfile(member, request)
        return ResponseEntity.ok(ApiResponse("프로필이 수정되었습니다", response))
    }

    @PutMapping("/password")
    fun changePassword(
        @CurrentMember member: Member,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        memberService.changePassword(
            member = member,
            currentPassword = request.currentPassword,
            newPassword = request.newPassword
        )
        return ResponseEntity.ok(ApiResponse("비밀번호가 변경되었습니다", Unit))
    }
}

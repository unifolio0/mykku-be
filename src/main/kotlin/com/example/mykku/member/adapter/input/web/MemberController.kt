package com.example.mykku.member.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.adapter.input.web.dto.ChangeMemberIdRequest
import com.example.mykku.member.adapter.input.web.dto.ChangePasswordRequest
import com.example.mykku.member.adapter.input.web.dto.CheckMemberIdRequest
import com.example.mykku.member.adapter.input.web.dto.CheckMemberIdResponse
import com.example.mykku.member.adapter.input.web.dto.MemberProfileResponse
import com.example.mykku.member.adapter.input.web.dto.SetupProfileRequest
import com.example.mykku.member.adapter.input.web.dto.UpdateProfileRequest
import com.example.mykku.member.adapter.input.web.dto.UpdateProfileWithImageRequest
import com.example.mykku.member.application.dto.MemberProfileResult
import com.example.mykku.member.application.port.input.ChangeMemberIdUseCase
import com.example.mykku.member.application.port.input.ChangePasswordUseCase
import com.example.mykku.member.application.port.input.CheckMemberIdUseCase
import com.example.mykku.member.application.port.input.GetMemberProfileUseCase
import com.example.mykku.member.application.port.input.SetupProfileUseCase
import com.example.mykku.member.application.port.input.UpdateMemberProfileUseCase
import com.example.mykku.member.application.port.input.WithdrawMemberUseCase
import com.example.mykku.member.domain.entity.Member
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val getMemberProfileUseCase: GetMemberProfileUseCase,
    private val updateMemberProfileUseCase: UpdateMemberProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val setupProfileUseCase: SetupProfileUseCase,
    private val checkMemberIdUseCase: CheckMemberIdUseCase,
    private val changeMemberIdUseCase: ChangeMemberIdUseCase,
    private val withdrawMemberUseCase: WithdrawMemberUseCase
) {

    @GetMapping("/me")
    fun getMyProfile(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        val result = getMemberProfileUseCase.getMyProfile(member)
        val response = MemberProfileResponse.from(result)
        return ResponseEntity.ok(ApiResponse("프로필 조회 성공", response))
    }

    @PatchMapping("/me", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProfile(
        @CurrentMember member: Member,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        return respondProfileUpdated(updateMemberProfileUseCase.updateProfile(member, request.toCommand()))
    }

    @PatchMapping("/me", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProfileWithImage(
        @CurrentMember member: Member,
        @RequestPart("request", required = false) @Valid request: UpdateProfileWithImageRequest?,
        @RequestPart("profileImage", required = false) profileImage: MultipartFile?
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        val command = (request ?: UpdateProfileWithImageRequest(nickname = null)).toCommand(profileImage)
        return respondProfileUpdated(updateMemberProfileUseCase.updateProfile(member, command))
    }

    @PatchMapping("/me/member-id")
    fun changeMemberId(
        @CurrentMember member: Member,
        @Valid @RequestBody request: ChangeMemberIdRequest
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        val result = changeMemberIdUseCase.changeMemberId(member, request.toCommand())
        val response = MemberProfileResponse.from(result)
        return ResponseEntity.ok(ApiResponse("아이디가 변경되었습니다", response))
    }

    @PutMapping("/password")
    fun changePassword(
        @CurrentMember member: Member,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        changePasswordUseCase.changePassword(member, request.toCommand())
        return ResponseEntity.ok(ApiResponse("비밀번호가 변경되었습니다", Unit))
    }

    @PostMapping("/setup-profile")
    fun setupProfile(
        @CurrentMember member: Member,
        @Valid @RequestBody request: SetupProfileRequest
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        val result = setupProfileUseCase.setupProfile(member, request.toCommand())
        val response = MemberProfileResponse.from(result)
        return ResponseEntity.ok(ApiResponse("프로필 설정이 완료되었습니다", response))
    }

    @DeleteMapping("/me")
    fun withdraw(
        @CurrentMember member: Member
    ): ResponseEntity<Unit> {
        withdrawMemberUseCase.execute(member.id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/check-id")
    fun checkMemberId(
        @Valid @RequestBody request: CheckMemberIdRequest
    ): ResponseEntity<ApiResponse<CheckMemberIdResponse>> {
        val available = checkMemberIdUseCase.checkAvailability(request.memberId)
        val response = CheckMemberIdResponse(
            memberId = request.memberId,
            available = available
        )
        return ResponseEntity.ok(ApiResponse("아이디 중복 확인 완료", response))
    }

    private fun respondProfileUpdated(
        result: MemberProfileResult
    ): ResponseEntity<ApiResponse<MemberProfileResponse>> {
        return ResponseEntity.ok(ApiResponse("프로필이 수정되었습니다", MemberProfileResponse.from(result)))
    }
}

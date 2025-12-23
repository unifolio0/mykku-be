package com.example.mykku.member

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.application.port.`in`.ChangePasswordCommand
import com.example.mykku.member.application.port.`in`.ChangePasswordUseCase
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.model.MemberId
import com.example.mykku.member.dto.ChangePasswordRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val changePasswordUseCase: ChangePasswordUseCase
) {

    @PutMapping("/password")
    fun changePassword(
        @CurrentMember member: Member,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        changePasswordUseCase.execute(
            ChangePasswordCommand(
                memberId = MemberId(member.id),
                currentPassword = request.currentPassword,
                newPassword = request.newPassword
            )
        )
        return ResponseEntity.ok(ApiResponse("비밀번호가 변경되었습니다", Unit))
    }
}

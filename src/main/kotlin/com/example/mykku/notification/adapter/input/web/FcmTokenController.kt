package com.example.mykku.notification.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.notification.application.dto.DeleteFcmTokenCommand
import com.example.mykku.notification.application.dto.RegisterFcmTokenCommand
import com.example.mykku.notification.application.port.input.ManageFcmTokenUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/fcm-tokens")
class FcmTokenController(
    private val manageFcmTokenUseCase: ManageFcmTokenUseCase
) {

    @PostMapping
    fun registerToken(
        @CurrentMember member: Member,
        @RequestBody @Valid request: RegisterFcmTokenRequest
    ): ResponseEntity<ApiResponse<FcmTokenResponse>> {
        val command = RegisterFcmTokenCommand(
            memberId = member.id.value,
            token = request.token,
            deviceId = request.deviceId,
            deviceType = request.deviceType
        )
        val result = manageFcmTokenUseCase.registerOrUpdateToken(command)

        return ResponseEntity.ok(
            ApiResponse(
                message = "FCM 토큰이 성공적으로 등록되었습니다.",
                data = FcmTokenResponse.from(result)
            )
        )
    }

    @GetMapping
    fun getTokens(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<FcmTokenResponse>>> {
        val tokens = manageFcmTokenUseCase.getTokens(member.id.value)

        return ResponseEntity.ok(
            ApiResponse(
                message = "FCM 토큰 목록을 성공적으로 조회했습니다.",
                data = tokens.map { FcmTokenResponse.from(it) }
            )
        )
    }

    @DeleteMapping("/{deviceId}")
    fun deleteToken(
        @CurrentMember member: Member,
        @PathVariable deviceId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        val command = DeleteFcmTokenCommand(member.id.value, deviceId)
        manageFcmTokenUseCase.deleteToken(command)

        return ResponseEntity.ok(
            ApiResponse(message = "FCM 토큰이 삭제되었습니다.", data = Unit)
        )
    }
}

package com.example.mykku.notification

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.Member
import com.example.mykku.notification.dto.FcmTokenResponse
import com.example.mykku.notification.dto.RegisterFcmTokenRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/fcm-tokens")
class FcmTokenController(
    private val fcmTokenService: FcmTokenService
) {

    @PostMapping
    fun registerToken(
        @CurrentMember member: Member,
        @RequestBody @Valid request: RegisterFcmTokenRequest
    ): ResponseEntity<ApiResponse<FcmTokenResponse>> {
        val response = fcmTokenService.registerOrUpdateToken(member, request)

        return ResponseEntity.ok(
            ApiResponse(
                message = "FCM 토큰이 성공적으로 등록되었습니다.",
                data = response
            )
        )
    }

    @GetMapping
    fun getTokens(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<FcmTokenResponse>>> {
        val tokens = fcmTokenService.getTokens(member)

        return ResponseEntity.ok(
            ApiResponse(
                message = "FCM 토큰 목록을 성공적으로 조회했습니다.",
                data = tokens
            )
        )
    }

    @DeleteMapping("/{deviceId}")
    fun deleteToken(
        @CurrentMember member: Member,
        @PathVariable deviceId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        fcmTokenService.deleteToken(member, deviceId)

        return ResponseEntity.ok(
            ApiResponse(message = "FCM 토큰이 삭제되었습니다.", data = Unit)
        )
    }
}

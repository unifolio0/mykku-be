package com.example.mykku.notification

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.Member
import com.example.mykku.notification.dto.NotificationSettingResponse
import com.example.mykku.notification.dto.UpdateNotificationSettingRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/notification-settings")
class NotificationSettingController(
    private val notificationSettingService: NotificationSettingService
) {

    @GetMapping
    fun getSettings(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<NotificationSettingResponse>>> {
        val settings = notificationSettingService.getSettings(member)

        return ResponseEntity.ok(
            ApiResponse(
                message = "알림 설정을 성공적으로 조회했습니다.",
                data = settings
            )
        )
    }

    @PatchMapping
    fun updateSetting(
        @CurrentMember member: Member,
        @RequestBody @Valid request: UpdateNotificationSettingRequest
    ): ResponseEntity<ApiResponse<NotificationSettingResponse>> {
        val response = notificationSettingService.updateSetting(member, request)

        return ResponseEntity.ok(
            ApiResponse(
                message = "알림 설정이 성공적으로 변경되었습니다.",
                data = response
            )
        )
    }
}

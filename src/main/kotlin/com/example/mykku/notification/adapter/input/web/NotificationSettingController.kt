package com.example.mykku.notification.adapter.input.web

import com.example.mykku.auth.config.CurrentMember
import com.example.mykku.common.dto.ApiResponse
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.notification.application.dto.GetNotificationSettingsQuery
import com.example.mykku.notification.application.dto.UpdateNotificationSettingCommand
import com.example.mykku.notification.application.port.input.ManageNotificationSettingUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notification-settings")
class NotificationSettingController(
    private val manageNotificationSettingUseCase: ManageNotificationSettingUseCase
) {

    @GetMapping
    fun getSettings(
        @CurrentMember member: Member
    ): ResponseEntity<ApiResponse<List<NotificationSettingResponse>>> {
        val query = GetNotificationSettingsQuery(member.id.value)
        val settings = manageNotificationSettingUseCase.getOrCreateSettings(query)

        return ResponseEntity.ok(
            ApiResponse(
                message = "알림 설정을 성공적으로 조회했습니다.",
                data = settings.map { NotificationSettingResponse.from(it) }
            )
        )
    }

    @PatchMapping
    fun updateSetting(
        @CurrentMember member: Member,
        @RequestBody @Valid request: UpdateNotificationSettingRequest
    ): ResponseEntity<ApiResponse<NotificationSettingResponse>> {
        val command = UpdateNotificationSettingCommand(
            memberId = member.id.value,
            notificationType = request.notificationType,
            isEnabled = request.isEnabled
        )
        val result = manageNotificationSettingUseCase.updateSetting(command)

        return ResponseEntity.ok(
            ApiResponse(
                message = "알림 설정이 성공적으로 변경되었습니다.",
                data = NotificationSettingResponse.from(result)
            )
        )
    }
}

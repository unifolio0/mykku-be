package com.example.mykku.notification.adapter.input.web

import com.example.mykku.notification.domain.vo.NotificationType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RegisterFcmTokenRequest(
    @field:NotBlank(message = "FCM 토큰은 필수입니다")
    val token: String,

    @field:NotBlank(message = "디바이스 ID는 필수입니다")
    val deviceId: String,

    val deviceType: String? = null
)

data class UpdateNotificationSettingRequest(
    @field:NotNull(message = "알림 타입은 필수입니다")
    val notificationType: NotificationType,

    @field:NotNull(message = "활성화 여부는 필수입니다")
    val isEnabled: Boolean
)

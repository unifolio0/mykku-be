package com.example.mykku.notification.dto

import com.example.mykku.notification.domain.NotificationType
import jakarta.validation.constraints.NotNull

data class UpdateNotificationSettingRequest(
    @field:NotNull(message = "알림 타입은 필수입니다")
    val notificationType: NotificationType,

    @field:NotNull(message = "활성화 여부는 필수입니다")
    val isEnabled: Boolean
)

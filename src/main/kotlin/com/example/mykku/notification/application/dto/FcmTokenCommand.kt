package com.example.mykku.notification.application.dto

data class RegisterFcmTokenCommand(
    val memberId: Long,
    val token: String,
    val deviceId: String,
    val deviceType: String? = null
)

data class DeleteFcmTokenCommand(
    val memberId: Long,
    val deviceId: String
)

package com.example.mykku.notification.application.dto

data class RegisterFcmTokenCommand(
    val memberId: String,
    val token: String,
    val deviceId: String,
    val deviceType: String? = null
)

data class DeleteFcmTokenCommand(
    val memberId: String,
    val deviceId: String
)

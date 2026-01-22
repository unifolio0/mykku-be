package com.example.mykku.member.application.dto

data class UpdateProfileCommand(
    val nickname: String?,
    val profileImage: String?
)

data class ChangePasswordCommand(
    val currentPassword: String,
    val newPassword: String
)

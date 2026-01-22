package com.example.mykku.role.application.dto

import java.time.LocalDateTime

data class ChangeRepresentativeRoleCommand(
    val memberId: String,
    val memberRoleId: Long
)

data class RoleResult(
    val id: Long,
    val name: String,
    val description: String?
)

data class MemberRoleResult(
    val id: Long,
    val role: RoleResult,
    val isRepresentative: Boolean,
    val earnedAt: LocalDateTime
)

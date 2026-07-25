package com.example.mykku.role.application.dto

import java.time.LocalDateTime

data class ChangeRepresentativeRoleCommand(
    val memberId: Long,
    val memberRoleId: Long
)

data class AcquireRoleCommand(
    val memberId: Long,
    val roleId: Long
)

data class AcquireRoleResult(
    val acquired: Boolean,
    val memberRole: MemberRoleResult
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

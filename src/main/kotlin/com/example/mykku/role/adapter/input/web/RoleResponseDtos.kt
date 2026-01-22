package com.example.mykku.role.adapter.input.web

import java.time.LocalDateTime

data class RoleResponse(
    val id: Long,
    val name: String,
    val description: String?
)

data class MemberRoleResponse(
    val id: Long,
    val role: RoleResponse,
    val isRepresentative: Boolean,
    val earnedAt: LocalDateTime
)

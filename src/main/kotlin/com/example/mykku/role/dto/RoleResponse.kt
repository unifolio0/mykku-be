package com.example.mykku.role.dto

import com.example.mykku.role.domain.Role

data class RoleResponse(
    val id: Long,
    val name: String,
    val description: String?
) {
    constructor(role: Role) : this(
        id = role.id!!,
        name = role.name,
        description = role.description
    )
}

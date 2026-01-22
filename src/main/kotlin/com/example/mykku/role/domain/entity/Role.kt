package com.example.mykku.role.domain.entity

import com.example.mykku.role.domain.vo.RoleId
import java.time.LocalDateTime

class Role private constructor(
    val id: RoleId,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun updateInfo(name: String, description: String?): Role {
        return Role(
            id = this.id,
            name = name,
            description = description,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now()
        )
    }

    companion object {
        fun create(
            name: String,
            description: String? = null
        ): Role {
            val now = LocalDateTime.now()
            return Role(
                id = RoleId(0),
                name = name,
                description = description,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: RoleId,
            name: String,
            description: String?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): Role {
            return Role(
                id = id,
                name = name,
                description = description,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

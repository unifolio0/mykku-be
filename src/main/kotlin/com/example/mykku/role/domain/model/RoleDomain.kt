package com.example.mykku.role.domain.model

import java.time.Instant

class RoleDomain private constructor(
    val id: RoleId?,
    private var _name: String,
    private var _description: String?,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {
    val name: String get() = _name
    val description: String? get() = _description
    val updatedAt: Instant get() = _updatedAt

    companion object {
        fun create(
            name: String,
            description: String?
        ): RoleDomain {
            val now = Instant.now()
            return RoleDomain(
                id = null,
                _name = name,
                _description = description,
                createdAt = now,
                _updatedAt = now
            )
        }

        fun reconstitute(
            id: RoleId,
            name: String,
            description: String?,
            createdAt: Instant,
            updatedAt: Instant
        ): RoleDomain {
            return RoleDomain(
                id = id,
                _name = name,
                _description = description,
                createdAt = createdAt,
                _updatedAt = updatedAt
            )
        }
    }

    fun update(name: String, description: String?) {
        _name = name
        _description = description
        _updatedAt = Instant.now()
    }
}

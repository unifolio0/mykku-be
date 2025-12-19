package com.example.mykku.role.domain.model

@JvmInline
value class RoleId(val value: Long) {
    init {
        require(value > 0) { "Role ID must be positive" }
    }
}

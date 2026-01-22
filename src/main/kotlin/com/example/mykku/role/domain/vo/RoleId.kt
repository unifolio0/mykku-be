package com.example.mykku.role.domain.vo

@JvmInline
value class RoleId(val value: Long) {
    companion object {
        fun of(value: Long): RoleId = RoleId(value)
    }
}

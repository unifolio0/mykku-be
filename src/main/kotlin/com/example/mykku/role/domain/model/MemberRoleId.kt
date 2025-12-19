package com.example.mykku.role.domain.model

@JvmInline
value class MemberRoleId(val value: Long) {
    init {
        require(value > 0) { "MemberRole ID must be positive" }
    }
}

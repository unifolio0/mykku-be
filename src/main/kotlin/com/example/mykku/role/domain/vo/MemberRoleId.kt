package com.example.mykku.role.domain.vo

@JvmInline
value class MemberRoleId(val value: Long) {
    companion object {
        fun of(value: Long): MemberRoleId = MemberRoleId(value)
    }
}

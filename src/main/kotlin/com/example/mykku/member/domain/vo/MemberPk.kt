package com.example.mykku.member.domain.vo

@JvmInline
value class MemberPk(val value: Long) {
    companion object {
        fun of(value: Long): MemberPk = MemberPk(value)
    }
}

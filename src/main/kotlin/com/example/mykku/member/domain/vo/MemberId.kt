package com.example.mykku.member.domain.vo

@JvmInline
value class MemberId(val value: String) {
    companion object {
        fun of(value: String): MemberId = MemberId(value)
    }
}

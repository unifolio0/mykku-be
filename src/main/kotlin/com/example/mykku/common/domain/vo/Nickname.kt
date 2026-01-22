package com.example.mykku.common.domain.vo

@JvmInline
value class Nickname(val value: String) {
    init {
        require(value.length in 2..20) { "닉네임은 2~20자여야 합니다" }
    }

    companion object {
        fun of(value: String): Nickname = Nickname(value)
    }
}

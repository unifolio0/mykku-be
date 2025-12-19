package com.example.mykku.member.domain.model

@JvmInline
value class Password(val value: String) {
    init {
        require(value.isNotBlank()) { "Password cannot be blank" }
    }
}
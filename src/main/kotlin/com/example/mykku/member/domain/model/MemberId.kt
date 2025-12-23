package com.example.mykku.member.domain.model

@JvmInline
value class MemberId(val value: String) {
    init {
        require(value.isNotBlank()) { "Member ID cannot be blank" }
    }

    companion object {
        fun generate(): MemberId = MemberId(java.util.UUID.randomUUID().toString())
    }
}
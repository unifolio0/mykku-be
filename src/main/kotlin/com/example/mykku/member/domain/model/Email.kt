package com.example.mykku.member.domain.model

@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotBlank()) { "Email cannot be blank" }
        require(EMAIL_PATTERN.matches(value)) { "Invalid email format" }
    }

    companion object {
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
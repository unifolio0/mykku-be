package com.example.mykku.member.domain.model

@JvmInline
value class Nickname(val value: String) {
    init {
        require(value.length <= MAX_LENGTH) { "Nickname must be $MAX_LENGTH characters or less" }
        require(VALID_PATTERN.matches(value)) { "Nickname can only contain Korean, English, numbers, and spaces" }
    }

    companion object {
        const val MAX_LENGTH = 10
        private val VALID_PATTERN = Regex("^[가-힣a-zA-Z0-9\\s]+$")
    }
}
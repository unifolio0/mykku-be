package com.example.mykku.email.util

import java.security.SecureRandom

object TemporaryPasswordGenerator {
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val DIGITS = "0123456789"
    private const val SPECIAL_CHARS = "!@#$%^&*"

    private val random = SecureRandom()

    fun generate(): String {
        val password = buildString {
            append(randomChars(UPPERCASE, 2))
            append(randomChars(LOWERCASE, 4))
            append(randomChars(DIGITS, 2))
            append(randomChars(SPECIAL_CHARS, 2))
        }

        return password.toList().shuffled(random).joinToString("")
    }

    private fun randomChars(source: String, count: Int): String {
        return (1..count)
            .map { source[random.nextInt(source.length)] }
            .joinToString("")
    }
}

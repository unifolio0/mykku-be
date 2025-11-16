package com.example.mykku.email.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TemporaryPasswordGeneratorTest {

    @Test
    fun `임시 비밀번호 생성 - 길이가 10자여야 함`() {
        val password = TemporaryPasswordGenerator.generate()

        assertEquals(10, password.length)
    }

    @Test
    fun `임시 비밀번호 생성 - 대문자를 포함해야 함`() {
        val password = TemporaryPasswordGenerator.generate()

        assertTrue(password.any { it.isUpperCase() })
    }

    @Test
    fun `임시 비밀번호 생성 - 소문자를 포함해야 함`() {
        val password = TemporaryPasswordGenerator.generate()

        assertTrue(password.any { it.isLowerCase() })
    }

    @Test
    fun `임시 비밀번호 생성 - 숫자를 포함해야 함`() {
        val password = TemporaryPasswordGenerator.generate()

        assertTrue(password.any { it.isDigit() })
    }

    @Test
    fun `임시 비밀번호 생성 - 특수문자를 포함해야 함`() {
        val password = TemporaryPasswordGenerator.generate()
        val specialChars = "!@#$%^&*"

        assertTrue(password.any { it in specialChars })
    }

    @Test
    fun `임시 비밀번호 생성 - 매번 다른 비밀번호를 생성해야 함`() {
        val password1 = TemporaryPasswordGenerator.generate()
        val password2 = TemporaryPasswordGenerator.generate()
        val password3 = TemporaryPasswordGenerator.generate()

        assertTrue(password1 != password2 || password2 != password3)
    }
}

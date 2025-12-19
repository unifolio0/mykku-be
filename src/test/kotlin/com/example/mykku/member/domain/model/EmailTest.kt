package com.example.mykku.member.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class EmailTest {

    @Test
    fun `유효한 이메일로 Email 생성 성공`() {
        val email = Email("user@example.com")

        assertThat(email.value).isEqualTo("user@example.com")
    }

    @ParameterizedTest
    @ValueSource(strings = ["test@domain.com", "user.name@company.co.kr", "a+b@test.org"])
    fun `다양한 유효 이메일 형식 테스트`(emailValue: String) {
        val email = Email(emailValue)

        assertThat(email.value).isEqualTo(emailValue)
    }

    @Test
    fun `빈 문자열로 Email 생성 시 예외 발생`() {
        assertThatThrownBy { Email("") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["invalid", "no-at-sign.com", "@no-local.com", "no-domain@"])
    fun `잘못된 이메일 형식으로 생성 시 예외 발생`(invalidEmail: String) {
        assertThatThrownBy { Email(invalidEmail) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}

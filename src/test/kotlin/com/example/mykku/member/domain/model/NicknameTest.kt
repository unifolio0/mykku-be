package com.example.mykku.member.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class NicknameTest {

    @Test
    fun `유효한 닉네임으로 Nickname 생성 성공`() {
        val nickname = Nickname("홍길동")

        assertThat(nickname.value).isEqualTo("홍길동")
    }

    @ParameterizedTest
    @ValueSource(strings = ["user123", "한글닉네임", "Mixed혼합", "1234567890"])
    fun `다양한 유효 닉네임 형식 테스트`(nicknameValue: String) {
        val nickname = Nickname(nicknameValue)

        assertThat(nickname.value).isEqualTo(nicknameValue)
    }

    @Test
    fun `10자 초과 닉네임으로 생성 시 예외 발생`() {
        assertThatThrownBy { Nickname("12345678901") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("10")
    }

    @ParameterizedTest
    @ValueSource(strings = ["특수문자!", "nick@name", "nick#name"])
    fun `허용되지 않는 문자 포함 시 예외 발생`(invalidNickname: String) {
        assertThatThrownBy { Nickname(invalidNickname) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `MAX_LENGTH 상수값 확인`() {
        assertThat(Nickname.MAX_LENGTH).isEqualTo(10)
    }
}

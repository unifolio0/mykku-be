package com.example.mykku.member.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class MemberIdTest {

    @Test
    fun `유효한 ID로 MemberId 생성 성공`() {
        val memberId = MemberId("user-123")

        assertThat(memberId.value).isEqualTo("user-123")
    }

    @Test
    fun `빈 문자열로 MemberId 생성 시 예외 발생`() {
        assertThatThrownBy { MemberId("") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("blank")
    }

    @Test
    fun `공백 문자열로 MemberId 생성 시 예외 발생`() {
        assertThatThrownBy { MemberId("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("blank")
    }

    @Test
    fun `generate 메서드로 UUID 형식의 ID 생성`() {
        val memberId = MemberId.generate()

        assertThat(memberId.value).isNotBlank()
        assertThat(memberId.value).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
    }
}

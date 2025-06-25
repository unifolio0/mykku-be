package com.example.mykku.member.domain

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MemberTest {
    @Test
    fun `Member의 nickname은 일정 길이 미만이어야 한다`() {
        assertThrows<MykkuException> {
            Member(
                id = "test-id",
                nickname = "a".repeat(Member.NICKNAME_MAX_LENGTH + 1),
                role = "USER",
                profileImage = "profile.jpg"
            )
        }.apply {
            assert(this.errorCode == ErrorCode.MEMBER_NICKNAME_TOO_LONG)
        }
    }

    @Test
    fun `Member의 nickname은 한글, 영문, 숫자만 사용할 수 있다`() {
        assertThrows<MykkuException> {
            Member(
                id = "test-id",
                nickname = "닉네임!@#",
                role = "USER",
                profileImage = "profile.jpg"
            )
        }.apply {
            assert(this.errorCode == ErrorCode.MEMBER_NICKNAME_INVALID_FORMAT)
        }
    }

    @Test
    fun `Member의 nickname에 특수문자가 포함되면 예외가 발생한다`() {
        assertThrows<MykkuException> {
            Member(
                id = "test-id",
                nickname = "nick_name",
                role = "USER",
                profileImage = "profile.jpg"
            )
        }.apply {
            assert(this.errorCode == ErrorCode.MEMBER_NICKNAME_INVALID_FORMAT)
        }
    }

    @Test
    fun `Member의 nickname에 공백이 포함되면 예외가 발생한다`() {
        assertThrows<MykkuException> {
            Member(
                id = "test-id",
                nickname = "nick name",
                role = "USER",
                profileImage = "profile.jpg"
            )
        }.apply {
            assert(this.errorCode == ErrorCode.MEMBER_NICKNAME_INVALID_FORMAT)
        }
    }
}
package com.example.mykku.member.domain

import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.exception.MemberErrorCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import com.example.mykku.role.domain.Role

class MemberTest {

    @Test
    fun `nickname이 최대 길이 10자일 때 성공한다`() {
        val validNickname = "r".repeat(Member.NICKNAME_MAX_LENGTH)

        Member(
            id = "test_member",
            nickname = validNickname,
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
    }

    @Test
    fun `nickname이 최대 길이를 초과하면 예외가 발생한다`() {
        val invalidNickname = "r".repeat(Member.NICKNAME_MAX_LENGTH + 1)

        val exception = assertThrows<MemberException> {
            Member(
                id = "test_member",
                nickname = invalidNickname,
                role = Role(name = "일반 덕후", description = "테스트용 칭호"),
                profileImage = "profile.jpg",
                provider = SocialProvider.GOOGLE,
                socialId = "12345",
                email = "test@example.com"
            )
        }

        assertEquals(MemberErrorCode.MEMBER_NICKNAME_TOO_LONG, exception.errorCode)
    }

    @Test
    fun `유효한 패턴의 nickname으로 생성할 수 있다`() {
        Member(
            id = "test_member",
            nickname = "한글123",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )

        Member(
            id = "test_member2",
            nickname = "English123",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )

        Member(
            id = "test_member3",
            nickname = "123456",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
    }

    @Test
    fun `특수문자가 포함된 nickname이면 예외가 발생한다`() {
        val exception = assertThrows<MemberException> {
            Member(
                id = "test_member",
                nickname = "닉네임!",
                role = Role(name = "일반 덕후", description = "테스트용 칭호"),
                profileImage = "profile.jpg",
                provider = SocialProvider.GOOGLE,
                socialId = "12345",
                email = "test@example.com"
            )
        }

        assertEquals(MemberErrorCode.MEMBER_NICKNAME_INVALID_FORMAT, exception.errorCode)
    }
}

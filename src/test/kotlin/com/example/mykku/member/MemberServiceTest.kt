package com.example.mykku.member

import com.example.mykku.BaseServiceTest
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.dto.UpdateProfileRequest
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.tool.MemberReader
import com.example.mykku.member.tool.MemberWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import com.example.mykku.role.domain.Role
import java.time.LocalDateTime

class MemberServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var memberReader: MemberReader

    @Mock
    private lateinit var memberWriter: MemberWriter

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var memberService: MemberService

    @Test
    fun `비밀번호 변경 성공`() {
        val currentPassword = "oldPassword123!"
        val newPassword = "newPassword123!"
        val encodedCurrentPassword = "encodedOldPassword"
        val encodedNewPassword = "encodedNewPassword"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = "test@example.com",
            password = encodedCurrentPassword
        )

        whenever(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true)
        whenever(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword)

        memberService.changePassword(member, currentPassword, newPassword)

        assertEquals(encodedNewPassword, member.password)
        verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword)
        verify(passwordEncoder).encode(newPassword)
        verify(memberWriter).save(member)
    }

    @Test
    fun `현재 비밀번호가 일치하지 않으면 예외 발생`() {
        val currentPassword = "wrongPassword"
        val newPassword = "newPassword123!"
        val encodedCurrentPassword = "encodedOldPassword"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = "test@example.com",
            password = encodedCurrentPassword
        )

        whenever(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(false)

        val exception = assertThrows<MemberException> {
            memberService.changePassword(member, currentPassword, newPassword)
        }

        assertEquals(MemberErrorCode.INVALID_CURRENT_PASSWORD, exception.errorCode)
    }

    @Test
    fun `비밀번호가 null인 경우 예외 발생`() {
        val currentPassword = "oldPassword123!"
        val newPassword = "newPassword123!"
        val member = Member(
            id = "memberId",
            nickname = "테스트",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "",
            provider = SocialProvider.EMAIL,
            socialId = null,
            email = "test@example.com",
            password = null
        )

        val exception = assertThrows<MemberException> {
            memberService.changePassword(member, currentPassword, newPassword)
        }

        assertEquals(MemberErrorCode.INVALID_CURRENT_PASSWORD, exception.errorCode)
    }

    @Test
    fun `프로필 조회 성공`() {
        val member = Member(
            id = "memberId",
            nickname = "테스트유저",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "https://example.com/profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )
        initializeBaseEntityFieldsFromSuperclass(member, LocalDateTime.now())

        val response = memberService.getMyProfile(member)

        assertEquals("memberId", response.id)
        assertEquals("테스트유저", response.nickname)
        assertEquals("https://example.com/profile.jpg", response.profileImage)
        assertEquals("test@example.com", response.email)
        assertEquals("일반 덕후", response.role)
    }

    @Test
    fun `프로필 수정 성공 - 닉네임과 프로필 이미지 변경`() {
        val member = Member(
            id = "memberId",
            nickname = "기존닉네임",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "https://example.com/old.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )
        initializeBaseEntityFieldsFromSuperclass(member, LocalDateTime.now())
        val request = UpdateProfileRequest(
            nickname = "새닉네임",
            profileImage = "https://example.com/new.jpg"
        )

        whenever(memberReader.existsByNickname("새닉네임")).thenReturn(false)

        val response = memberService.updateProfile(member, request)

        assertEquals("새닉네임", response.nickname)
        assertEquals("https://example.com/new.jpg", response.profileImage)
        verify(memberWriter).save(member)
    }

    @Test
    fun `프로필 수정 - 동일한 닉네임으로 변경 시 중복 체크 안함`() {
        val member = Member(
            id = "memberId",
            nickname = "테스트유저",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "https://example.com/old.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )
        initializeBaseEntityFieldsFromSuperclass(member, LocalDateTime.now())
        val request = UpdateProfileRequest(
            nickname = "테스트유저",
            profileImage = null
        )

        val response = memberService.updateProfile(member, request)

        assertEquals("테스트유저", response.nickname)
        verify(memberReader, never()).existsByNickname("테스트유저")
        verify(memberWriter).save(member)
    }

    @Test
    fun `프로필 수정 실패 - 닉네임 중복`() {
        val member = Member(
            id = "memberId",
            nickname = "기존닉네임",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "https://example.com/old.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )
        val request = UpdateProfileRequest(
            nickname = "중복닉네임",
            profileImage = null
        )

        whenever(memberReader.existsByNickname("중복닉네임")).thenReturn(true)

        val exception = assertThrows<MemberException> {
            memberService.updateProfile(member, request)
        }

        assertEquals(MemberErrorCode.NICKNAME_ALREADY_EXISTS, exception.errorCode)
    }

    @Test
    fun `프로필 수정 실패 - 닉네임 길이 초과`() {
        val member = Member(
            id = "memberId",
            nickname = "기존닉네임",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "https://example.com/old.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )
        val request = UpdateProfileRequest(
            nickname = "가나다라마바사아자차카",
            profileImage = null
        )

        whenever(memberReader.existsByNickname("가나다라마바사아자차카")).thenReturn(false)

        val exception = assertThrows<MemberException> {
            memberService.updateProfile(member, request)
        }

        assertEquals(MemberErrorCode.MEMBER_NICKNAME_TOO_LONG, exception.errorCode)
    }

    @Test
    fun `프로필 수정 실패 - 닉네임 형식 오류`() {
        val member = Member(
            id = "memberId",
            nickname = "기존닉네임",
            role = Role(name = "일반 덕후", description = "테스트용 칭호"),
            profileImage = "https://example.com/old.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "123456",
            email = "test@example.com"
        )
        val request = UpdateProfileRequest(
            nickname = "특수문자!@#",
            profileImage = null
        )

        whenever(memberReader.existsByNickname("특수문자!@#")).thenReturn(false)

        val exception = assertThrows<MemberException> {
            memberService.updateProfile(member, request)
        }

        assertEquals(MemberErrorCode.MEMBER_NICKNAME_INVALID_FORMAT, exception.errorCode)
    }
}

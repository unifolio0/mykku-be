package com.example.mykku.member.tool

import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

@ExtendWith(MockitoExtension::class)
class MemberWriterTest {

    @Mock
    private lateinit var memberRepository: MemberRepository

    @InjectMocks
    private lateinit var memberWriter: MemberWriter

    @Test
    fun `save는 멤버를 저장하고 저장된 결과를 반환한다`() {
        val member = Member(
            id = "member123",
            nickname = "테스트유저",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
        val savedMember = Member(
            id = "member123",
            nickname = "테스트유저",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )

        whenever(memberRepository.save(member)).thenReturn(savedMember)

        val result = memberWriter.save(member)

        assertSame(savedMember, result)
        assertEquals(member.id, result.id)
        assertEquals(member.nickname, result.nickname)
        assertEquals(member.email, result.email)
        verify(memberRepository).save(member)
    }

    @Test
    fun `save는 기존 멤버를 업데이트하고 저장된 결과를 반환한다`() {
        val existingMember = Member(
            id = "member123",
            nickname = "기존닉네임",
            role = "USER",
            profileImage = "old_profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "old@example.com"
        )
        val updatedMember = Member(
            id = "member123",
            nickname = "새로운닉네임",
            role = "ADMIN",
            profileImage = "new_profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "new@example.com"
        )

        whenever(memberRepository.save(existingMember)).thenReturn(updatedMember)

        val result = memberWriter.save(existingMember)

        assertSame(updatedMember, result)
        assertEquals(existingMember.id, result.id)
        verify(memberRepository).save(existingMember)
    }
}
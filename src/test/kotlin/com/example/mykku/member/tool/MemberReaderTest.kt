package com.example.mykku.member.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import com.example.mykku.member.repository.MemberRepository
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever

class MemberReaderTest : BaseToolTest() {

    @Mock
    private lateinit var memberRepository: MemberRepository

    @InjectMocks
    private lateinit var memberReader: MemberReader

    @Test
    fun `getMemberById는 유효한 memberId로 멤버를 반환한다`() {
        val memberId = "member123"
        val member = createMockMember(memberId, "테스트유저")

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))

        val result = memberReader.getMemberById(memberId)

        assertEquals(member, result)
        assertEquals(memberId, result.id)
        assertEquals("테스트유저", result.nickname)
    }

    @Test
    fun `getMemberById는 존재하지 않는 memberId로 MEMBER_NOT_FOUND 예외를 발생시킨다`() {
        val memberId = "nonexistent"

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.empty())

        val exception = assertThrows<MemberException> {
            memberReader.getMemberById(memberId)
        }

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `findById는 유효한 memberId로 Optional Member를 반환한다`() {
        val memberId = "member123"
        val member = createMockMember(memberId, "테스트유저")

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))

        val result = memberReader.findById(memberId)

        assertTrue(result.isPresent)
        assertEquals(member, result.get())
    }

    @Test
    fun `findById는 존재하지 않는 memberId로 빈 Optional을 반환한다`() {
        val memberId = "nonexistent"

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.empty())

        val result = memberReader.findById(memberId)

        assertFalse(result.isPresent)
    }

    @Test
    fun `existsByNickname은 존재하는 닉네임에 대해 true를 반환한다`() {
        val nickname = "기존닉네임"

        whenever(memberRepository.existsByNickname(nickname)).thenReturn(true)

        val result = memberReader.existsByNickname(nickname)

        assertTrue(result)
    }

    @Test
    fun `existsByNickname은 존재하지 않는 닉네임에 대해 false를 반환한다`() {
        val nickname = "새로운닉네임"

        whenever(memberRepository.existsByNickname(nickname)).thenReturn(false)

        val result = memberReader.existsByNickname(nickname)

        assertFalse(result)
    }
}

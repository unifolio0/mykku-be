package com.example.mykku.member.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Follow
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.FollowRepository
import com.example.mykku.member.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class MemberReaderTest {

    @Mock
    private lateinit var followRepository: FollowRepository

    @Mock
    private lateinit var memberRepository: MemberRepository

    @InjectMocks
    private lateinit var memberReader: MemberReader

    private fun createMockMember(id: String, nickname: String): Member {
        return Member(
            id = id,
            nickname = nickname,
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
    }

    @Test
    fun `getFollowerByMemberId는 팔로워 목록을 반환한다`() {
        val followerId = "follower123"
        val following1 = createMockMember("following1", "팔로잉1")
        val following2 = createMockMember("following2", "팔로잉2")
        
        val follow1 = Follow(
            id = 1L,
            follower = createMockMember(followerId, "팔로워"),
            following = following1
        )
        val follow2 = Follow(
            id = 2L,
            follower = createMockMember(followerId, "팔로워"),
            following = following2
        )
        val follows = listOf(follow1, follow2)

        whenever(followRepository.findByFollowerId(followerId)).thenReturn(follows)

        val result = memberReader.getFollowerByMemberId(followerId)

        assertEquals(2, result.size)
        assertEquals(following1, result[0])
        assertEquals(following2, result[1])
    }

    @Test
    fun `getFollowerByMemberId는 팔로워가 없을 때 빈 목록을 반환한다`() {
        val memberId = "member123"

        whenever(followRepository.findByFollowerId(memberId)).thenReturn(emptyList())

        val result = memberReader.getFollowerByMemberId(memberId)

        assertTrue(result.isEmpty())
    }

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

        val exception = assertThrows<MykkuException> {
            memberReader.getMemberById(memberId)
        }

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.errorCode)
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
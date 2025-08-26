package com.example.mykku.dailymessage.repository

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DataJpaTest
class DailyMessageCommentRepositoryTest {

    @Autowired
    private lateinit var dailyMessageCommentRepository: DailyMessageCommentRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var dailyMessageRepository: DailyMessageRepository

    private fun createTestData(): Triple<Member, DailyMessage, DailyMessageComment> {
        val member = Member(
            id = "test_member",
            nickname = "테스트유저",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
        memberRepository.save(member)

        val dailyMessage = DailyMessage(
            title = "오늘의 메시지",
            content = "테스트 메시지",
            date = LocalDate.now()
        )
        dailyMessageRepository.save(dailyMessage)

        val comment = DailyMessageComment(
            content = "테스트 댓글",
            dailyMessage = dailyMessage,
            member = member
        )
        return Triple(member, dailyMessage, dailyMessageCommentRepository.save(comment))
    }

    @Test
    fun `findByIdAndDailyMessageId는 댓글 ID와 데일리메시지 ID로 댓글을 조회한다`() {
        val (_, dailyMessage, comment) = createTestData()

        val result = dailyMessageCommentRepository.findByIdAndDailyMessageId(comment.id!!, dailyMessage.id!!)

        assertNotNull(result)
        assertEquals(comment.id, result.id)
        assertEquals(comment.content, result.content)
    }

    @Test
    fun `findByIdAndDailyMessageId는 존재하지 않는 댓글 ID로 조회하면 null을 반환한다`() {
        val (_, dailyMessage, _) = createTestData()

        val result = dailyMessageCommentRepository.findByIdAndDailyMessageId(999L, dailyMessage.id!!)

        assertNull(result)
    }

    @Test
    fun `findByIdAndDailyMessageId는 다른 데일리메시지 ID로 조회하면 null을 반환한다`() {
        val (_, _, comment) = createTestData()

        val result = dailyMessageCommentRepository.findByIdAndDailyMessageId(comment.id!!, 999L)

        assertNull(result)
    }

    @Test
    fun `findByDailyMessage는 특정 데일리메시지의 모든 댓글을 조회한다`() {
        val (member, dailyMessage, comment1) = createTestData()

        val comment2 = DailyMessageComment(
            content = "두 번째 댓글",
            dailyMessage = dailyMessage,
            member = member
        )
        dailyMessageCommentRepository.save(comment2)

        val result = dailyMessageCommentRepository.findByDailyMessage(dailyMessage)

        assertEquals(2, result.size)
        assertEquals(comment1.content, result.find { it.id == comment1.id }?.content)
        assertEquals(comment2.content, result.find { it.id == comment2.id }?.content)
    }

    @Test
    fun `findByDailyMessage는 댓글이 없는 데일리메시지의 경우 빈 리스트를 반환한다`() {
        val member = Member(
            id = "test_member2",
            nickname = "테스트유저2",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "54321",
            email = "test2@example.com"
        )
        memberRepository.save(member)

        val emptyDailyMessage = DailyMessage(
            title = "댓글 없는 메시지",
            content = "테스트",
            date = LocalDate.now().plusDays(1)
        )
        dailyMessageRepository.save(emptyDailyMessage)

        val result = dailyMessageCommentRepository.findByDailyMessage(emptyDailyMessage)

        assertEquals(0, result.size)
    }
}

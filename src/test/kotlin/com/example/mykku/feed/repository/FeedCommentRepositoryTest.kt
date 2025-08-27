package com.example.mykku.feed.repository

import com.example.mykku.board.domain.Board
import com.example.mykku.board.repository.BoardRepository
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import com.example.mykku.member.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
class FeedCommentRepositoryTest {

    @Autowired
    private lateinit var feedCommentRepository: FeedCommentRepository

    @Autowired
    private lateinit var feedRepository: FeedRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var boardRepository: BoardRepository

    private fun createTestData(): Triple<Feed, FeedComment, FeedComment> {
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

        val board = Board(
            title = "테스트보드",
            logo = "logo.jpg"
        )
        boardRepository.save(board)

        val feed = Feed(
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
        feedRepository.save(feed)

        val parentComment = FeedComment(
            content = "부모 댓글",
            feed = feed,
            member = member
        )
        feedCommentRepository.save(parentComment)

        val childComment = FeedComment(
            content = "자식 댓글",
            feed = feed,
            parentComment = parentComment,
            member = member
        )
        feedCommentRepository.save(childComment)

        return Triple(feed, parentComment, childComment)
    }

    @Test
    fun `findByFeedAndParentCommentIsNull은 피드의 최상위 댓글만 조회한다`() {
        val (feed, parentComment, _) = createTestData()
        val pageable = PageRequest.of(0, 10)

        val result = feedCommentRepository.findByFeedAndParentCommentIsNull(feed, pageable)

        assertEquals(1, result.content.size)
        assertEquals(parentComment.id, result.content[0].id)
        assertEquals("부모 댓글", result.content[0].content)
    }

    @Test
    fun `findByFeedAndParentCommentIsNull은 댓글이 없는 피드의 경우 빈 페이지를 반환한다`() {
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

        val board = Board(
            title = "테스트보드2",
            logo = "logo.jpg"
        )
        boardRepository.save(board)

        val emptyFeed = Feed(
            title = "빈 피드",
            content = "댓글 없음",
            board = board,
            member = member
        )
        feedRepository.save(emptyFeed)

        val pageable = PageRequest.of(0, 10)
        val result = feedCommentRepository.findByFeedAndParentCommentIsNull(emptyFeed, pageable)

        assertTrue(result.content.isEmpty())
    }

    @Test
    fun `findByParentComment는 특정 부모 댓글의 자식 댓글들을 조회한다`() {
        val (feed, parentComment, childComment) = createTestData()
        
        val anotherChildComment = FeedComment(
            content = "또 다른 자식 댓글",
            feed = feed,
            parentComment = parentComment,
            member = parentComment.member
        )
        feedCommentRepository.save(anotherChildComment)

        val result = feedCommentRepository.findByParentComment(parentComment)

        assertEquals(2, result.size)
        assertTrue(result.any { it.id == childComment.id && it.content == "자식 댓글" })
        assertTrue(result.any { it.id == anotherChildComment.id && it.content == "또 다른 자식 댓글" })
    }

    @Test
    fun `findByParentComment는 자식 댓글이 없는 부모 댓글의 경우 빈 리스트를 반환한다`() {
        val (feed, _, _) = createTestData()
        
        val lonelyParent = FeedComment(
            content = "외로운 부모 댓글",
            feed = feed,
            member = feed.member
        )
        feedCommentRepository.save(lonelyParent)

        val result = feedCommentRepository.findByParentComment(lonelyParent)

        assertEquals(0, result.size)
    }

    @Test
    fun `findByParentCommentIn은 여러 부모 댓글들의 자식 댓글들을 조회한다`() {
        val (feed, parentComment1, childComment1) = createTestData()
        
        val parentComment2 = FeedComment(
            content = "두 번째 부모 댓글",
            feed = feed,
            member = feed.member
        )
        feedCommentRepository.save(parentComment2)
        
        val childComment2 = FeedComment(
            content = "두 번째 자식 댓글",
            feed = feed,
            parentComment = parentComment2,
            member = feed.member
        )
        feedCommentRepository.save(childComment2)

        val parentComments = listOf(parentComment1, parentComment2)
        val result = feedCommentRepository.findByParentCommentIn(parentComments)

        assertEquals(2, result.size)
        assertTrue(result.any { it.id == childComment1.id && it.content == "자식 댓글" })
        assertTrue(result.any { it.id == childComment2.id && it.content == "두 번째 자식 댓글" })
    }

    @Test
    fun `findByParentCommentIn은 자식 댓글이 없는 부모 댓글들의 경우 빈 리스트를 반환한다`() {
        val (feed, _, _) = createTestData()
        
        val lonelyParent1 = FeedComment(
            content = "외로운 부모1",
            feed = feed,
            member = feed.member
        )
        feedCommentRepository.save(lonelyParent1)
        
        val lonelyParent2 = FeedComment(
            content = "외로운 부모2",
            feed = feed,
            member = feed.member
        )
        feedCommentRepository.save(lonelyParent2)

        val result = feedCommentRepository.findByParentCommentIn(listOf(lonelyParent1, lonelyParent2))

        assertEquals(0, result.size)
    }
}
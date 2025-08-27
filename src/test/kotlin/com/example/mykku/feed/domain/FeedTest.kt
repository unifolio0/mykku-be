package com.example.mykku.feed.domain

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class FeedTest {

    private fun createTestMemberAndBoard(): Pair<Member, Board> {
        val member = Member(
            id = "test_member",
            nickname = "테스트유저",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
        
        val board = Board(title = "테스트보드", logo = "logo.jpg")
        
        return Pair(member, board)
    }

    @Test
    fun `content가 최대 길이 1000자일 때 성공한다`() {
        val (member, board) = createTestMemberAndBoard()
        val validContent = "r".repeat(Feed.CONTENT_MAX_LENGTH)
        
        Feed(
            title = "테스트 피드",
            content = validContent,
            board = board,
            member = member
        )
    }

    @Test
    fun `content가 최대 길이를 초과하면 예외가 발생한다`() {
        val (member, board) = createTestMemberAndBoard()
        val invalidContent = "r".repeat(Feed.CONTENT_MAX_LENGTH + 1)

        val exception = assertThrows<MykkuException> {
            Feed(
                title = "테스트 피드",
                content = invalidContent,
                board = board,
                member = member
            )
        }
        
        assertEquals(ErrorCode.FEED_CONTENT_TOO_LONG, exception.errorCode)
    }
}
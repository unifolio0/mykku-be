package com.example.mykku.feed.domain

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class FeedTagTest {

    private fun createTestFeed(): Feed {
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
        
        return Feed(
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )
    }

    @Test
    fun `title이 최대 길이 20자일 때 성공한다`() {
        val feed = createTestFeed()
        val validTitle = "r".repeat(FeedTag.TITLE_MAX_LENGTH)
        
        FeedTag(feed = feed, title = validTitle)
    }

    @Test
    fun `title이 최대 길이를 초과하면 예외가 발생한다`() {
        val feed = createTestFeed()
        val invalidTitle = "r".repeat(FeedTag.TITLE_MAX_LENGTH + 1)

        val exception = assertThrows<MykkuException> {
            FeedTag(feed = feed, title = invalidTitle)
        }
        
        assertEquals(ErrorCode.TAG_TITLE_TOO_LONG, exception.errorCode)
    }

    @Test
    fun `유효한 패턴의 title로 생성할 수 있다`() {
        val feed = createTestFeed()
        
        FeedTag(feed = feed, title = "한글123")
        FeedTag(feed = feed, title = "English123")
        FeedTag(feed = feed, title = "123456")
    }

    @Test
    fun `특수문자가 포함된 title이면 예외가 발생한다`() {
        val feed = createTestFeed()
        
        val exception = assertThrows<MykkuException> {
            FeedTag(feed = feed, title = "태그!")
        }
        
        assertEquals(ErrorCode.TAG_INVALID_FORMAT, exception.errorCode)
    }

    @Test
    fun `공백이 포함된 title이면 예외가 발생한다`() {
        val feed = createTestFeed()
        
        val exception = assertThrows<MykkuException> {
            FeedTag(feed = feed, title = "태그 테스트")
        }
        
        assertEquals(ErrorCode.TAG_INVALID_FORMAT, exception.errorCode)
    }
}
package com.example.mykku.feed.domain

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock

class FeedCommentTest {

    private val feed: Feed = mock()
    private val member: Member = mock()

    @Test
    fun `FeedComment의 content는 일정 길이 미만이어야 한다`() {
        assertThrows<MykkuException> {
            FeedComment(
                content = "a".repeat(FeedComment.CONTENT_MAX_LENGTH + 1),
                feed = feed,
                member = member
            )
        }.apply {
            assert(this.errorCode == ErrorCode.FEED_COMMENT_CONTENT_TOO_LONG)
        }
    }
}

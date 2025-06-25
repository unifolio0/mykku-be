package com.example.mykku.feed.domain

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.member.domain.Member
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock

class FeedTest {

    private val board: Board = mock()
    private val member: Member = mock()

    @Test
    fun `Feed의 content는 일정 길이 미만이어야 한다`() {
        assertThrows<MykkuException> {
            Feed(
                title = "테스트 피드",
                content = "a".repeat(Feed.CONTENT_MAX_LENGTH + 1),
                board = board,
                member = member
            )
        }.apply {
            assert(this.errorCode == ErrorCode.FEED_CONTENT_TOO_LONG)
        }
    }

    @Test
    fun `Feed의 이미지는 최대 개수를 초과할 수 없다`() {
        val feed = Feed(
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )

        val images = (1..Feed.IMAGE_MAX_COUNT + 1).map {
            FeedImage(url = "image$it.jpg", feed = feed)
        }.toMutableList()

        assertThrows<MykkuException> {
            Feed(
                title = "테스트 피드",
                content = "테스트 내용",
                board = board,
                member = member,
                feedImages = images
            )
        }.apply {
            assert(this.errorCode == ErrorCode.FEED_IMAGE_LIMIT_EXCEEDED)
        }
    }

    @Test
    fun `Feed의 태그는 최대 개수를 초과할 수 없다`() {
        val feed = Feed(
            title = "테스트 피드",
            content = "테스트 내용",
            board = board,
            member = member
        )

        val tag: Tag = mock()
        val tags = (1..Feed.TAG_MAX_COUNT + 1).map {
            FeedTag(feed = feed, tag = tag)
        }.toMutableList()

        assertThrows<MykkuException> {
            Feed(
                title = "테스트 피드",
                content = "테스트 내용",
                board = board,
                member = member,
                feedTags = tags
            )
        }.apply {
            assert(this.errorCode == ErrorCode.FEED_TAG_LIMIT_EXCEEDED)
        }
    }
}

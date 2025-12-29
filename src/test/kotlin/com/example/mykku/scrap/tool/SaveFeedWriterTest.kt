package com.example.mykku.scrap.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.feed.domain.Feed
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveFeed
import com.example.mykku.scrap.exception.ScrapErrorCode
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveFeedRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SaveFeedWriterTest : BaseToolTest() {

    @Mock
    private lateinit var saveFeedRepository: SaveFeedRepository

    @Mock
    private lateinit var saveFeedReader: SaveFeedReader

    @InjectMocks
    private lateinit var saveFeedWriter: SaveFeedWriter

    @Test
    fun `saveFeed는 피드를 저장한다`() {
        // given
        val member = createMockMember()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = createMockBoard(),
            member = member
        )
        val folder = Folder(id = 1L, member = member, name = "폴더", description = null)
        val savedFeed = SaveFeed(id = 1L, member = member, feed = feed, folder = folder)

        whenever(saveFeedReader.isSaved(member, feed))
            .thenReturn(false)
        whenever(saveFeedRepository.save(any<SaveFeed>()))
            .thenReturn(savedFeed)

        // when
        val result = saveFeedWriter.saveFeed(member, feed, folder)

        // then
        assertSame(savedFeed, result)
        verify(saveFeedRepository).save(any<SaveFeed>())
    }

    @Test
    fun `saveFeed는 이미 저장된 피드면 예외를 발생시킨다`() {
        // given
        val member = createMockMember()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = createMockBoard(),
            member = member
        )
        val folder = Folder(id = 1L, member = member, name = "폴더", description = null)

        whenever(saveFeedReader.isSaved(member, feed))
            .thenReturn(true)

        // when & then
        val exception = assertThrows<ScrapException> {
            saveFeedWriter.saveFeed(member, feed, folder)
        }

        assertEquals(ScrapErrorCode.SAVE_FEED_ALREADY_EXISTS, exception.errorCode)
    }

    @Test
    fun `updateFolder는 폴더를 변경한다`() {
        // given
        val member = createMockMember()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = createMockBoard(),
            member = member
        )
        val oldFolder = Folder(id = 1L, member = member, name = "원래 폴더", description = null)
        val newFolder = Folder(id = 2L, member = member, name = "새 폴더", description = null)
        val saveFeed = SaveFeed(id = 1L, member = member, feed = feed, folder = oldFolder)

        whenever(saveFeedRepository.findByMemberAndFeed(member, feed))
            .thenReturn(saveFeed)
        whenever(saveFeedRepository.save(saveFeed))
            .thenReturn(saveFeed)

        // when
        saveFeedWriter.updateFolder(member, feed, newFolder)

        // then
        assertEquals(newFolder, saveFeed.folder)
        verify(saveFeedRepository).save(saveFeed)
    }

    @Test
    fun `updateFolder는 저장되지 않은 피드면 예외를 발생시킨다`() {
        // given
        val member = createMockMember()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = createMockBoard(),
            member = member
        )
        val folder = Folder(id = 1L, member = member, name = "폴더", description = null)

        whenever(saveFeedRepository.findByMemberAndFeed(member, feed))
            .thenReturn(null)

        // when & then
        val exception = assertThrows<ScrapException> {
            saveFeedWriter.updateFolder(member, feed, folder)
        }

        assertEquals(ScrapErrorCode.SAVE_FEED_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `unsaveFeed는 피드 저장을 취소한다`() {
        // given
        val member = createMockMember()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = createMockBoard(),
            member = member
        )

        whenever(saveFeedReader.isSaved(member, feed))
            .thenReturn(true)

        // when
        saveFeedWriter.unsaveFeed(member, feed)

        // then
        verify(saveFeedRepository).deleteByMemberAndFeed(member, feed)
    }

    @Test
    fun `unsaveFeed는 저장되지 않은 피드면 예외를 발생시킨다`() {
        // given
        val member = createMockMember()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = createMockBoard(),
            member = member
        )

        whenever(saveFeedReader.isSaved(member, feed))
            .thenReturn(false)

        // when & then
        val exception = assertThrows<ScrapException> {
            saveFeedWriter.unsaveFeed(member, feed)
        }

        assertEquals(ScrapErrorCode.SAVE_FEED_NOT_FOUND, exception.errorCode)
    }
}

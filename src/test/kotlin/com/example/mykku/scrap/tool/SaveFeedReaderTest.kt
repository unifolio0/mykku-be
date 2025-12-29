package com.example.mykku.scrap.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveFeed
import com.example.mykku.scrap.repository.SaveFeedRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveFeedReaderTest : BaseToolTest() {

    @Mock
    private lateinit var saveFeedRepository: SaveFeedRepository

    @Mock
    private lateinit var memberRepository: MemberRepository

    @InjectMocks
    private lateinit var saveFeedReader: SaveFeedReader

    @Test
    fun `isSaved는 Member 객체로 저장 여부를 확인한다`() {
        // given
        val member = createMockMember()
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = createMockBoard(),
            member = member
        )

        whenever(saveFeedRepository.existsByMemberAndFeed(member, feed))
            .thenReturn(true)

        // when
        val result = saveFeedReader.isSaved(member, feed)

        // then
        assertTrue(result)
    }

    @Test
    fun `isSaved는 String memberId로 저장 여부를 확인한다`() {
        // given
        val memberId = "testMember"
        val member = createMockMember(id = memberId)
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = createMockBoard(),
            member = member
        )

        whenever(memberRepository.findById(memberId))
            .thenReturn(Optional.of(member))
        whenever(saveFeedRepository.existsByMemberAndFeed(member, feed))
            .thenReturn(false)

        // when
        val result = saveFeedReader.isSaved(memberId, feed)

        // then
        assertFalse(result)
    }

    @Test
    fun `isSaved는 존재하지 않는 memberId면 false를 반환한다`() {
        // given
        val memberId = "nonExistentMember"
        val feed = Feed(
            id = 1L,
            title = "테스트 피드",
            content = "내용",
            board = createMockBoard(),
            member = createMockMember()
        )

        whenever(memberRepository.findById(memberId))
            .thenReturn(Optional.empty())

        // when
        val result = saveFeedReader.isSaved(memberId, feed)

        // then
        assertFalse(result)
    }

    @Test
    fun `getSavedFeedsByMember는 Member 객체로 저장된 피드 ID 목록을 반환한다`() {
        // given
        val member = createMockMember()
        val feeds = listOf(
            Feed(id = 1L, title = "피드1", content = "내용1", board = createMockBoard(), member = member),
            Feed(id = 2L, title = "피드2", content = "내용2", board = createMockBoard(), member = member),
            Feed(id = 3L, title = "피드3", content = "내용3", board = createMockBoard(), member = member)
        )
        val feedIds = feeds.mapNotNull { it.id }
        val folder = Folder(id = 1L, member = member, name = "폴더", description = null)

        val savedFeeds = listOf(
            SaveFeed(id = 1L, member = member, feed = feeds[0], folder = folder),
            SaveFeed(id = 2L, member = member, feed = feeds[2], folder = folder)
        )

        whenever(saveFeedRepository.findByMemberAndFeedIdIn(member, feedIds))
            .thenReturn(savedFeeds)

        // when
        val result = saveFeedReader.getSavedFeedsByMember(member, feeds)

        // then
        assertEquals(setOf(1L, 3L), result)
    }

    @Test
    fun `getSavedFeedsByMember는 String memberId로 저장된 피드 ID 목록을 반환한다`() {
        // given
        val memberId = "testMember"
        val member = createMockMember(id = memberId)
        val feeds = listOf(
            Feed(id = 1L, title = "피드1", content = "내용1", board = createMockBoard(), member = member)
        )
        val feedIds = feeds.mapNotNull { it.id }
        val folder = Folder(id = 1L, member = member, name = "폴더", description = null)
        val savedFeeds = listOf(
            SaveFeed(id = 1L, member = member, feed = feeds[0], folder = folder)
        )

        whenever(memberRepository.findById(memberId))
            .thenReturn(Optional.of(member))
        whenever(saveFeedRepository.findByMemberAndFeedIdIn(member, feedIds))
            .thenReturn(savedFeeds)

        // when
        val result = saveFeedReader.getSavedFeedsByMember(memberId, feeds)

        // then
        assertEquals(setOf(1L), result)
    }

    @Test
    fun `getSavedFeeds는 회원의 저장된 피드를 페이지로 반환한다`() {
        // given
        val member = createMockMember()
        val folder = Folder(id = 1L, member = member, name = "폴더", description = null)
        val feed1 = Feed(id = 1L, title = "피드1", content = "내용1", board = createMockBoard(), member = member)
        val feed2 = Feed(id = 2L, title = "피드2", content = "내용2", board = createMockBoard(), member = member)

        val savedFeeds = listOf(
            SaveFeed(id = 1L, member = member, feed = feed1, folder = folder),
            SaveFeed(id = 2L, member = member, feed = feed2, folder = folder)
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(savedFeeds, pageable, savedFeeds.size.toLong())

        whenever(saveFeedRepository.findByMember(member, pageable))
            .thenReturn(page)

        // when
        val result = saveFeedReader.getSavedFeeds(member, pageable)

        // then
        assertEquals(2, result.content.size)
        assertEquals(2L, result.totalElements)
    }

    @Test
    fun `getSavedFeedsByFolder는 폴더별 저장된 피드를 반환한다`() {
        // given
        val member = createMockMember()
        val folder = Folder(id = 1L, member = member, name = "폴더", description = null)
        val feed = Feed(id = 1L, title = "피드", content = "내용", board = createMockBoard(), member = member)
        val savedFeeds = listOf(
            SaveFeed(id = 1L, member = member, feed = feed, folder = folder)
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(savedFeeds, pageable, savedFeeds.size.toLong())

        whenever(saveFeedRepository.findByMemberAndFolder(member, folder, pageable))
            .thenReturn(page)

        // when
        val result = saveFeedReader.getSavedFeedsByFolder(member, folder, pageable)

        // then
        assertEquals(1, result.content.size)
        assertEquals(folder.id, result.content[0].folder.id)
    }
}

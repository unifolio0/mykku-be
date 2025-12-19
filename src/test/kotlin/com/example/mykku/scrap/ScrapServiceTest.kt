package com.example.mykku.scrap

import com.example.mykku.BaseServiceTest
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.application.port.out.DailyMessageQueryPort
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.application.port.out.FanNoteQueryPort
import com.example.mykku.event.domain.Event
import com.example.mykku.event.application.port.out.EventQueryPort
import com.example.mykku.feed.application.port.out.FeedQueryPort
import com.example.mykku.feed.domain.Feed
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveDailyMessage
import com.example.mykku.scrap.domain.SaveEvent
import com.example.mykku.scrap.domain.SaveFanNote
import com.example.mykku.scrap.domain.SaveFeed
import com.example.mykku.scrap.dto.SaveFeedRequest
import com.example.mykku.scrap.dto.UpdateSaveFeedFolderRequest
import com.example.mykku.scrap.application.port.out.*
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ScrapServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var saveFeedQueryPort: SaveFeedQueryPort

    @Mock
    private lateinit var saveFeedRepositoryPort: SaveFeedRepositoryPort

    @Mock
    private lateinit var saveDailyMessageQueryPort: SaveDailyMessageQueryPort

    @Mock
    private lateinit var saveDailyMessageRepositoryPort: SaveDailyMessageRepositoryPort

    @Mock
    private lateinit var saveEventQueryPort: SaveEventQueryPort

    @Mock
    private lateinit var saveEventRepositoryPort: SaveEventRepositoryPort

    @Mock
    private lateinit var saveFanNoteQueryPort: SaveFanNoteQueryPort

    @Mock
    private lateinit var saveFanNoteRepositoryPort: SaveFanNoteRepositoryPort

    @Mock
    private lateinit var folderQueryPort: FolderQueryPort

    @Mock
    private lateinit var feedQueryPort: FeedQueryPort

    @Mock
    private lateinit var dailyMessageQueryPort: DailyMessageQueryPort

    @Mock
    private lateinit var eventQueryPort: EventQueryPort

    @Mock
    private lateinit var fanNoteQueryPort: FanNoteQueryPort

    @InjectMocks
    private lateinit var scrapService: ScrapService

    @Test
    fun `saveFeed는 피드를 폴더에 저장한다`() {
        // given
        val member = createTestMember()
        val feedId = 1L
        val folderId = 10L
        val request = SaveFeedRequest(folderId = folderId)
        val feed = Feed(
            id = feedId,
            title = "테스트 피드",
            content = "내용",
            board = createTestBoard(),
            member = member
        )
        val folder = Folder(id = folderId, member = member, name = "폴더", description = null)

        whenever(feedQueryPort.getFeedById(feedId)).thenReturn(feed)
        whenever(folderQueryPort.getFolderById(folderId, member)).thenReturn(folder)

        // when
        scrapService.saveFeed(feedId, request, member)

        // then
        verify(feedQueryPort).getFeedById(feedId)
        verify(folderQueryPort).getFolderById(folderId, member)
        verify(saveFeedRepositoryPort).saveFeed(member, feed, folder)
    }

    @Test
    fun `unsaveFeed는 피드 저장을 취소한다`() {
        // given
        val member = createTestMember()
        val feedId = 1L
        val feed = Feed(
            id = feedId,
            title = "테스트 피드",
            content = "내용",
            board = createTestBoard(),
            member = member
        )

        whenever(feedQueryPort.getFeedById(feedId)).thenReturn(feed)

        // when
        scrapService.unsaveFeed(feedId, member)

        // then
        verify(feedQueryPort).getFeedById(feedId)
        verify(saveFeedRepositoryPort).unsaveFeed(member, feed)
    }

    @Test
    fun `updateSaveFeedFolder는 저장된 피드의 폴더를 변경한다`() {
        // given
        val member = createTestMember()
        val feedId = 1L
        val newFolderId = 20L
        val request = UpdateSaveFeedFolderRequest(folderId = newFolderId)
        val feed = Feed(
            id = feedId,
            title = "테스트 피드",
            content = "내용",
            board = createTestBoard(),
            member = member
        )
        val newFolder = Folder(id = newFolderId, member = member, name = "새 폴더", description = null)

        whenever(feedQueryPort.getFeedById(feedId)).thenReturn(feed)
        whenever(folderQueryPort.getFolderById(newFolderId, member)).thenReturn(newFolder)

        // when
        scrapService.updateSaveFeedFolder(feedId, request, member)

        // then
        verify(feedQueryPort).getFeedById(feedId)
        verify(folderQueryPort).getFolderById(newFolderId, member)
        verify(saveFeedRepositoryPort).updateFolder(member, feed, newFolder)
    }

    @Test
    fun `getSavedFeeds는 폴더 ID가 있으면 해당 폴더의 피드만 반환한다`() {
        // given
        val member = createTestMember()
        val folderId = 10L
        val folder = Folder(id = folderId, member = member, name = "폴더", description = null)
        val feed = Feed(
            id = 1L,
            title = "피드",
            content = "내용",
            board = createTestBoard(),
            member = member
        )
        val saveFeeds = listOf(
            SaveFeed(id = 1L, member = member, feed = feed, folder = folder)
        )
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(saveFeeds, pageable, 1)

        whenever(folderQueryPort.getFolderById(folderId, member)).thenReturn(folder)
        whenever(saveFeedQueryPort.getSavedFeedsByFolder(member, folder, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedFeeds(member, folderId, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(folderQueryPort).getFolderById(folderId, member)
        verify(saveFeedQueryPort).getSavedFeedsByFolder(member, folder, pageable)
    }

    @Test
    fun `getSavedFeeds는 폴더 ID가 없으면 모든 저장된 피드를 반환한다`() {
        // given
        val member = createTestMember()
        val folder = Folder(id = 1L, member = member, name = "폴더", description = null)
        val feed = Feed(
            id = 1L,
            title = "피드",
            content = "내용",
            board = createTestBoard(),
            member = member
        )
        val saveFeeds = listOf(
            SaveFeed(id = 1L, member = member, feed = feed, folder = folder)
        )
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(saveFeeds, pageable, 1)

        whenever(saveFeedQueryPort.getSavedFeedsByFolder(member, null, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedFeeds(member, null, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(saveFeedQueryPort).getSavedFeedsByFolder(member, null, pageable)
    }

    @Test
    fun `saveDailyMessage는 하루덕담을 저장한다`() {
        // given
        val member = createTestMember()
        val dailyMessageId = 1L
        val dailyMessage = DailyMessage(
            id = dailyMessageId,
            title = "덕담 제목",
            content = "테스트 메시지",
            date = LocalDate.now()
        )

        whenever(dailyMessageQueryPort.getDailyMessage(dailyMessageId)).thenReturn(dailyMessage)

        // when
        scrapService.saveDailyMessage(dailyMessageId, member)

        // then
        verify(dailyMessageQueryPort).getDailyMessage(dailyMessageId)
        verify(saveDailyMessageRepositoryPort).saveDailyMessage(member, dailyMessage)
    }

    @Test
    fun `unsaveDailyMessage는 하루덕담 저장을 취소한다`() {
        // given
        val member = createTestMember()
        val dailyMessageId = 1L
        val dailyMessage = DailyMessage(
            id = dailyMessageId,
            title = "덕담 제목",
            content = "테스트 메시지",
            date = LocalDate.now()
        )

        whenever(dailyMessageQueryPort.getDailyMessage(dailyMessageId)).thenReturn(dailyMessage)

        // when
        scrapService.unsaveDailyMessage(dailyMessageId, member)

        // then
        verify(dailyMessageQueryPort).getDailyMessage(dailyMessageId)
        verify(saveDailyMessageRepositoryPort).unsaveDailyMessage(member, dailyMessage)
    }

    @Test
    fun `getSavedDailyMessages는 저장된 하루덕담 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val dailyMessage = DailyMessage(
            id = 1L,
            title = "덕담 제목",
            content = "메시지",
            date = LocalDate.now()
        )
        val saveDailyMessages = listOf(
            SaveDailyMessage(id = 1L, member = member, dailyMessage = dailyMessage)
        )
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(saveDailyMessages, pageable, 1)

        whenever(saveDailyMessageQueryPort.getSavedDailyMessages(member, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedDailyMessages(member, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(saveDailyMessageQueryPort).getSavedDailyMessages(member, pageable)
    }

    @Test
    fun `saveEvent는 이벤트를 저장한다`() {
        // given
        val member = createTestMember()
        val eventId = 1L
        val event = Event(
            id = eventId,
            title = "테스트 이벤트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        whenever(eventQueryPort.getEventById(eventId)).thenReturn(event)

        // when
        scrapService.saveEvent(eventId, member)

        // then
        verify(eventQueryPort).getEventById(eventId)
        verify(saveEventRepositoryPort).saveEvent(member, event)
    }

    @Test
    fun `unsaveEvent는 이벤트 저장을 취소한다`() {
        // given
        val member = createTestMember()
        val eventId = 1L
        val event = Event(
            id = eventId,
            title = "테스트 이벤트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        whenever(eventQueryPort.getEventById(eventId)).thenReturn(event)

        // when
        scrapService.unsaveEvent(eventId, member)

        // then
        verify(eventQueryPort).getEventById(eventId)
        verify(saveEventRepositoryPort).unsaveEvent(member, event)
    }

    @Test
    fun `getSavedEvents는 저장된 이벤트 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val event = Event(
            id = 1L,
            title = "이벤트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val saveEvents = listOf(
            SaveEvent(id = 1L, member = member, event = event)
        )
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(saveEvents, pageable, 1)

        whenever(saveEventQueryPort.getSavedEvents(member, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedEvents(member, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(saveEventQueryPort).getSavedEvents(member, pageable)
    }

    @Test
    fun `saveFanNote는 덕질노트를 저장한다`() {
        // given
        val member = createTestMember()
        val fanNoteId = 1L
        val fanNote = FanNote(
            id = fanNoteId,
            title = "테스트 덕질노트",
            subtitle = null,
            content = null,
            productionDate = LocalDate.now(),
            coverImageUrl = null
        )

        whenever(fanNoteQueryPort.findById(fanNoteId)).thenReturn(fanNote)

        // when
        scrapService.saveFanNote(fanNoteId, member)

        // then
        verify(fanNoteQueryPort).findById(fanNoteId)
        verify(saveFanNoteRepositoryPort).saveFanNote(member, fanNote)
    }

    @Test
    fun `unsaveFanNote는 덕질노트 저장을 취소한다`() {
        // given
        val member = createTestMember()
        val fanNoteId = 1L
        val fanNote = FanNote(
            id = fanNoteId,
            title = "테스트 덕질노트",
            subtitle = null,
            content = null,
            productionDate = LocalDate.now(),
            coverImageUrl = null
        )

        whenever(fanNoteQueryPort.findById(fanNoteId)).thenReturn(fanNote)

        // when
        scrapService.unsaveFanNote(fanNoteId, member)

        // then
        verify(fanNoteQueryPort).findById(fanNoteId)
        verify(saveFanNoteRepositoryPort).unsaveFanNote(member, fanNote)
    }

    @Test
    fun `getSavedFanNotes는 저장된 덕질노트 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val fanNote = FanNote(
            id = 1L,
            title = "덕질노트",
            subtitle = null,
            content = null,
            productionDate = LocalDate.now(),
            coverImageUrl = null
        )
        val saveFanNotes = listOf(
            SaveFanNote(id = 1L, member = member, fanNote = fanNote)
        )
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(saveFanNotes, pageable, 1)

        whenever(saveFanNoteQueryPort.getSavedFanNotes(member, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedFanNotes(member, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(saveFanNoteQueryPort).getSavedFanNotes(member, pageable)
    }
}

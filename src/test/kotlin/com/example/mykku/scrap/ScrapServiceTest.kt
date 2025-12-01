package com.example.mykku.scrap

import com.example.mykku.BaseServiceTest
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.fannote.tool.FanNoteReader
import com.example.mykku.event.domain.Event
import com.example.mykku.event.tool.EventReader
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveDailyMessage
import com.example.mykku.scrap.domain.SaveEvent
import com.example.mykku.scrap.domain.SaveFanNote
import com.example.mykku.scrap.domain.SaveFeed
import com.example.mykku.scrap.dto.SaveFeedRequest
import com.example.mykku.scrap.dto.UpdateSaveFeedFolderRequest
import com.example.mykku.scrap.tool.*
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
    private lateinit var saveFeedReader: SaveFeedReader

    @Mock
    private lateinit var saveFeedWriter: SaveFeedWriter

    @Mock
    private lateinit var saveDailyMessageReader: SaveDailyMessageReader

    @Mock
    private lateinit var saveDailyMessageWriter: SaveDailyMessageWriter

    @Mock
    private lateinit var saveEventReader: SaveEventReader

    @Mock
    private lateinit var saveEventWriter: SaveEventWriter

    @Mock
    private lateinit var saveFanNoteReader: SaveFanNoteReader

    @Mock
    private lateinit var saveFanNoteWriter: SaveFanNoteWriter

    @Mock
    private lateinit var folderReader: FolderReader

    @Mock
    private lateinit var feedReader: FeedReader

    @Mock
    private lateinit var dailyMessageReader: DailyMessageReader

    @Mock
    private lateinit var eventReader: EventReader

    @Mock
    private lateinit var fanNoteReader: FanNoteReader

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

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(folderReader.getFolderById(folderId, member)).thenReturn(folder)

        // when
        scrapService.saveFeed(feedId, request, member)

        // then
        verify(feedReader).getFeedById(feedId)
        verify(folderReader).getFolderById(folderId, member)
        verify(saveFeedWriter).saveFeed(member, feed, folder)
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

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)

        // when
        scrapService.unsaveFeed(feedId, member)

        // then
        verify(feedReader).getFeedById(feedId)
        verify(saveFeedWriter).unsaveFeed(member, feed)
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

        whenever(feedReader.getFeedById(feedId)).thenReturn(feed)
        whenever(folderReader.getFolderById(newFolderId, member)).thenReturn(newFolder)

        // when
        scrapService.updateSaveFeedFolder(feedId, request, member)

        // then
        verify(feedReader).getFeedById(feedId)
        verify(folderReader).getFolderById(newFolderId, member)
        verify(saveFeedWriter).updateFolder(member, feed, newFolder)
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

        whenever(folderReader.getFolderById(folderId, member)).thenReturn(folder)
        whenever(saveFeedReader.getSavedFeedsByFolder(member, folder, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedFeeds(member, folderId, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(folderReader).getFolderById(folderId, member)
        verify(saveFeedReader).getSavedFeedsByFolder(member, folder, pageable)
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

        whenever(saveFeedReader.getSavedFeedsByFolder(member, null, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedFeeds(member, null, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(saveFeedReader).getSavedFeedsByFolder(member, null, pageable)
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

        whenever(dailyMessageReader.getDailyMessage(dailyMessageId)).thenReturn(dailyMessage)

        // when
        scrapService.saveDailyMessage(dailyMessageId, member)

        // then
        verify(dailyMessageReader).getDailyMessage(dailyMessageId)
        verify(saveDailyMessageWriter).saveDailyMessage(member, dailyMessage)
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

        whenever(dailyMessageReader.getDailyMessage(dailyMessageId)).thenReturn(dailyMessage)

        // when
        scrapService.unsaveDailyMessage(dailyMessageId, member)

        // then
        verify(dailyMessageReader).getDailyMessage(dailyMessageId)
        verify(saveDailyMessageWriter).unsaveDailyMessage(member, dailyMessage)
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

        whenever(saveDailyMessageReader.getSavedDailyMessages(member, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedDailyMessages(member, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(saveDailyMessageReader).getSavedDailyMessages(member, pageable)
    }

    @Test
    fun `saveEvent는 이벤트를 저장한다`() {
        // given
        val member = createTestMember()
        val eventId = 1L
        val event = Event(
            id = eventId,
            title = "테스트 이벤트",
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        whenever(eventReader.getEventById(eventId)).thenReturn(event)

        // when
        scrapService.saveEvent(eventId, member)

        // then
        verify(eventReader).getEventById(eventId)
        verify(saveEventWriter).saveEvent(member, event)
    }

    @Test
    fun `unsaveEvent는 이벤트 저장을 취소한다`() {
        // given
        val member = createTestMember()
        val eventId = 1L
        val event = Event(
            id = eventId,
            title = "테스트 이벤트",
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        whenever(eventReader.getEventById(eventId)).thenReturn(event)

        // when
        scrapService.unsaveEvent(eventId, member)

        // then
        verify(eventReader).getEventById(eventId)
        verify(saveEventWriter).unsaveEvent(member, event)
    }

    @Test
    fun `getSavedEvents는 저장된 이벤트 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val event = Event(
            id = 1L,
            title = "이벤트",
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val saveEvents = listOf(
            SaveEvent(id = 1L, member = member, event = event)
        )
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(saveEvents, pageable, 1)

        whenever(saveEventReader.getSavedEvents(member, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedEvents(member, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(saveEventReader).getSavedEvents(member, pageable)
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

        whenever(fanNoteReader.findById(fanNoteId)).thenReturn(fanNote)

        // when
        scrapService.saveFanNote(fanNoteId, member)

        // then
        verify(fanNoteReader).findById(fanNoteId)
        verify(saveFanNoteWriter).saveFanNote(member, fanNote)
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

        whenever(fanNoteReader.findById(fanNoteId)).thenReturn(fanNote)

        // when
        scrapService.unsaveFanNote(fanNoteId, member)

        // then
        verify(fanNoteReader).findById(fanNoteId)
        verify(saveFanNoteWriter).unsaveFanNote(member, fanNote)
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

        whenever(saveFanNoteReader.getSavedFanNotes(member, pageable)).thenReturn(page)

        // when
        val result = scrapService.getSavedFanNotes(member, pageable)

        // then
        assertEquals(1, result.content.size)
        verify(saveFanNoteReader).getSavedFanNotes(member, pageable)
    }
}

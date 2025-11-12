package com.example.mykku.scrap.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.feed.domain.Event
import com.example.mykku.scrap.domain.SaveEvent
import com.example.mykku.scrap.repository.SaveEventRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveEventReaderTest : BaseToolTest() {

    @Mock
    private lateinit var saveEventRepository: SaveEventRepository

    @InjectMocks
    private lateinit var saveEventReader: SaveEventReader

    @Test
    fun `isSaved는 저장 여부를 확인한다`() {
        val member = createMockMember()
        val event = Event(
            id = 1L,
            title = "테스트 이벤트",
            isContest = false,
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        whenever(saveEventRepository.existsByMemberAndEvent(member, event))
            .thenReturn(true)

        val result = saveEventReader.isSaved(member, event)

        assertTrue(result)
    }

    @Test
    fun `isSaved는 저장되지 않은 경우 false를 반환한다`() {
        val member = createMockMember()
        val event = Event(
            id = 1L,
            title = "테스트 이벤트",
            isContest = false,
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        whenever(saveEventRepository.existsByMemberAndEvent(member, event))
            .thenReturn(false)

        val result = saveEventReader.isSaved(member, event)

        assertFalse(result)
    }

    @Test
    fun `getSavedEventIds는 저장된 이벤트 ID 목록을 반환한다`() {
        val member = createMockMember()
        val event1 = Event(id = 1L, title = "이벤트1", isContest = false, expiredAt = LocalDateTime.now().plusDays(7))
        val event2 = Event(id = 2L, title = "이벤트2", isContest = false, expiredAt = LocalDateTime.now().plusDays(7))
        val event3 = Event(id = 3L, title = "이벤트3", isContest = false, expiredAt = LocalDateTime.now().plusDays(7))
        val events = listOf(event1, event2, event3)

        val savedEvents = listOf(
            SaveEvent(id = 1L, member = member, event = event1),
            SaveEvent(id = 2L, member = member, event = event3)
        )

        whenever(saveEventRepository.findByMemberAndEventIn(member, events))
            .thenReturn(savedEvents)

        val result = saveEventReader.getSavedEventIds(member, events)

        assertEquals(setOf(1L, 3L), result)
    }

    @Test
    fun `getSavedEventIds는 저장된 이벤트가 없으면 빈 Set을 반환한다`() {
        val member = createMockMember()
        val event1 = Event(id = 1L, title = "이벤트1", isContest = false, expiredAt = LocalDateTime.now().plusDays(7))
        val events = listOf(event1)

        whenever(saveEventRepository.findByMemberAndEventIn(member, events))
            .thenReturn(emptyList())

        val result = saveEventReader.getSavedEventIds(member, events)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getSavedEvents는 회원의 저장된 이벤트를 페이지로 반환한다`() {
        val member = createMockMember()
        val event1 = Event(id = 1L, title = "이벤트1", isContest = false, expiredAt = LocalDateTime.now().plusDays(7))
        val event2 = Event(id = 2L, title = "이벤트2", isContest = false, expiredAt = LocalDateTime.now().plusDays(7))

        val savedEvents = listOf(
            SaveEvent(id = 1L, member = member, event = event1),
            SaveEvent(id = 2L, member = member, event = event2)
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(savedEvents, pageable, savedEvents.size.toLong())

        whenever(saveEventRepository.findByMember(member, pageable))
            .thenReturn(page)

        val result = saveEventReader.getSavedEvents(member, pageable)

        assertEquals(2, result.content.size)
        assertEquals(2L, result.totalElements)
    }
}

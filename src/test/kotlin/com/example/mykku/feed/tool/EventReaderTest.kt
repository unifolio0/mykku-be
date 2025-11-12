package com.example.mykku.feed.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage
import com.example.mykku.feed.repository.EventImageRepository
import com.example.mykku.feed.repository.EventRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import com.example.mykku.feed.domain.EventSortType
import com.example.mykku.feed.domain.EventStatusType
import com.example.mykku.feed.domain.EventTag
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.repository.EventTagRepository
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional

class EventReaderTest : BaseToolTest() {

    @Mock
    private lateinit var eventRepository: EventRepository

    @Mock
    private lateinit var eventImageRepository: EventImageRepository

    @Mock
    private lateinit var eventTagRepository: EventTagRepository

    @InjectMocks
    private lateinit var eventReader: EventReader

    private fun createMockEvent(id: Long): Event {
        return Event(
            id = id,
            isContest = false,
            title = "테스트 이벤트 $id",
            expiredAt = LocalDateTime.now().plusDays(7)
        )
    }

    @Test
    fun `getProcessingEventPreviews는 진행 중인 이벤트 미리보기 목록을 반환한다`() {
        val event1 = createMockEvent(1L)
        val event2 = createMockEvent(2L)
        val events = listOf(event1, event2)

        val eventImage1 = EventImage(
            id = 1L,
            url = "https://example.com/event1_image1.jpg",
            orderIndex = 0,
            event = event1
        )
        val eventImage2 = EventImage(
            id = 2L,
            url = "https://example.com/event2_image1.jpg",
            orderIndex = 0,
            event = event2
        )
        val eventImages = listOf(eventImage1, eventImage2)

        whenever(eventRepository.findByExpiredAtAfter(any<LocalDateTime>())).thenReturn(events)
        whenever(eventImageRepository.findByEventIn(events)).thenReturn(eventImages)

        val result = eventReader.getProcessingEventPreviews()

        assertEquals(2, result.size)
        assertEquals(event1.id, result[0].id)
        assertEquals(1, result[0].images.size)
        assertEquals(eventImage1.url, result[0].images[0])

        assertEquals(event2.id, result[1].id)
        assertEquals(1, result[1].images.size)
        assertEquals(eventImage2.url, result[1].images[0])
    }

    @Test
    fun `getProcessingEventPreviews는 이미지가 없는 이벤트에 대해 빈 이미지 목록을 반환한다`() {
        val event = createMockEvent(1L)
        val events = listOf(event)

        whenever(eventRepository.findByExpiredAtAfter(any<LocalDateTime>())).thenReturn(events)
        whenever(eventImageRepository.findByEventIn(events)).thenReturn(emptyList())

        val result = eventReader.getProcessingEventPreviews()

        assertEquals(1, result.size)
        assertEquals(event.id, result[0].id)
        assertTrue(result[0].images.isEmpty())
    }

    @Test
    fun `getProcessingEventPreviews는 이벤트가 없을 때 빈 목록을 반환한다`() {
        whenever(eventRepository.findByExpiredAtAfter(any<LocalDateTime>())).thenReturn(emptyList())
        whenever(eventImageRepository.findByEventIn(emptyList())).thenReturn(emptyList())

        val result = eventReader.getProcessingEventPreviews()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getProcessingEventPreviews는 최대 5개의 이벤트만 반환한다`() {
        val events = (1..10L).map { createMockEvent(it) }
        val limitedEvents = events.take(5)

        whenever(eventRepository.findByExpiredAtAfter(any<LocalDateTime>())).thenReturn(limitedEvents)
        whenever(eventImageRepository.findByEventIn(limitedEvents)).thenReturn(emptyList())

        val result = eventReader.getProcessingEventPreviews()

        assertEquals(5, result.size)
    }


    @Test
    fun `getEventsWithPagination은 active 상태로 최신순 정렬하여 이벤트를 조회한다`() {
        val event1 = createMockEvent(1L)
        val event2 = createMockEvent(2L)
        val events = listOf(event1, event2)
        val pageable = PageRequest.of(0, 20)
        val eventPage = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findByExpiredAtAfterOrderByCreatedAtDesc(any(), any())).thenReturn(eventPage)

        val result = eventReader.getEventsWithPagination(EventStatusType.ACTIVE, EventSortType.LATEST, pageable, LocalDateTime.now())

        assertEquals(2, result.totalElements)
        assertEquals(events, result.content)
    }

    @Test
    fun `getEventsWithPagination은 active 상태로 오래된순 정렬하여 이벤트를 조회한다`() {
        val event1 = createMockEvent(1L)
        val event2 = createMockEvent(2L)
        val events = listOf(event1, event2)
        val pageable = PageRequest.of(0, 20)
        val eventPage = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findByExpiredAtAfterOrderByCreatedAtAsc(any(), any())).thenReturn(eventPage)

        val result = eventReader.getEventsWithPagination(EventStatusType.ACTIVE, EventSortType.OLDEST, pageable, LocalDateTime.now())

        assertEquals(2, result.totalElements)
        assertEquals(events, result.content)
    }

    @Test
    fun `getEventsWithPagination은 active 상태로 인기순 정렬하여 이벤트를 조회한다`() {
        val event1 = createMockEvent(1L)
        val event2 = createMockEvent(2L)
        val events = listOf(event1, event2)
        val pageable = PageRequest.of(0, 20)
        val eventPage = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findActiveEventsByPopular(any(), any())).thenReturn(eventPage)

        val result = eventReader.getEventsWithPagination(EventStatusType.ACTIVE, EventSortType.POPULAR, pageable, LocalDateTime.now())

        assertEquals(2, result.totalElements)
        assertEquals(events, result.content)
    }

    @Test
    fun `getEventsWithPagination은 expired 상태로 이벤트를 조회한다`() {
        val event1 = createMockEvent(1L)
        val events = listOf(event1)
        val pageable = PageRequest.of(0, 20)
        val eventPage = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(any(), any())).thenReturn(eventPage)

        val result = eventReader.getEventsWithPagination(EventStatusType.EXPIRED, EventSortType.LATEST, pageable, LocalDateTime.now())

        assertEquals(1, result.totalElements)
        assertEquals(events, result.content)
    }

    @Test
    fun `getEventsWithPagination은 all 상태로 모든 이벤트를 조회한다`() {
        val event1 = createMockEvent(1L)
        val event2 = createMockEvent(2L)
        val events = listOf(event1, event2)
        val pageable = PageRequest.of(0, 20)
        val eventPage = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findAllByOrderByCreatedAtDesc(any())).thenReturn(eventPage)

        val result = eventReader.getEventsWithPagination(EventStatusType.ALL, EventSortType.LATEST, pageable, LocalDateTime.now())

        assertEquals(2, result.totalElements)
        assertEquals(events, result.content)
    }

    @Test
    fun `getEventByIdWithRelations는 이벤트를 조회한다`() {
        val event = createMockEvent(1L)

        whenever(eventRepository.findById(1L)).thenReturn(Optional.of(event))

        val result = eventReader.getEventByIdWithRelations(1L)

        assertEquals(event, result)
    }

    @Test
    fun `getEventByIdWithRelations는 존재하지 않는 이벤트 조회 시 예외를 발생시킨다`() {
        whenever(eventRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows<FeedException> {
            eventReader.getEventByIdWithRelations(999L)
        }
    }

    @Test
    fun `getEventImages는 이벤트별로 이미지를 그룹화하여 반환한다`() {
        val event1 = createMockEvent(1L)
        val event2 = createMockEvent(2L)
        val events = listOf(event1, event2)

        val image1 = EventImage(id = 1L, url = "url1", orderIndex = 0, event = event1)
        val image2 = EventImage(id = 2L, url = "url2", orderIndex = 1, event = event1)
        val image3 = EventImage(id = 3L, url = "url3", orderIndex = 0, event = event2)

        whenever(eventImageRepository.findByEventIn(events)).thenReturn(listOf(image1, image2, image3))

        val result = eventReader.getEventImages(events)

        assertEquals(2, result.size)
        assertEquals(2, result[1L]?.size)
        assertEquals(1, result[2L]?.size)
    }

    @Test
    fun `getEventTags는 이벤트별로 태그를 그룹화하여 반환한다`() {
        val event1 = createMockEvent(1L)
        val event2 = createMockEvent(2L)
        val events = listOf(event1, event2)

        val tag1 = EventTag(id = 1L, title = "tag1", event = event1)
        val tag2 = EventTag(id = 2L, title = "tag2", event = event1)
        val tag3 = EventTag(id = 3L, title = "tag3", event = event2)

        whenever(eventTagRepository.findByEventIn(events)).thenReturn(listOf(tag1, tag2, tag3))

        val result = eventReader.getEventTags(events)

        assertEquals(2, result.size)
        assertEquals(2, result[1L]?.size)
        assertEquals(1, result[2L]?.size)
    }
}

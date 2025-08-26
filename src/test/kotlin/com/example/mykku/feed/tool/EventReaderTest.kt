package com.example.mykku.feed.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage
import com.example.mykku.feed.repository.EventImageRepository
import com.example.mykku.feed.repository.EventRepository
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SocialProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class EventReaderTest {

    @Mock
    private lateinit var eventRepository: EventRepository

    @Mock
    private lateinit var eventImageRepository: EventImageRepository

    @InjectMocks
    private lateinit var eventReader: EventReader

    private fun createMockMember(): Member {
        return Member(
            id = "member123",
            nickname = "테스트유저",
            role = "USER",
            profileImage = "profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "12345",
            email = "test@example.com"
        )
    }

    private fun createMockBoard(): Board {
        return Board(
            id = 1L,
            title = "테스트보드",
            logo = "logo.jpg"
        )
    }

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

        whenever(eventRepository.getByEventPreviews(any<LocalDateTime>())).thenReturn(events)
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

        whenever(eventRepository.getByEventPreviews(any<LocalDateTime>())).thenReturn(events)
        whenever(eventImageRepository.findByEventIn(events)).thenReturn(emptyList())

        val result = eventReader.getProcessingEventPreviews()

        assertEquals(1, result.size)
        assertEquals(event.id, result[0].id)
        assertTrue(result[0].images.isEmpty())
    }

    @Test
    fun `getProcessingEventPreviews는 이벤트가 없을 때 빈 목록을 반환한다`() {
        whenever(eventRepository.getByEventPreviews(any<LocalDateTime>())).thenReturn(emptyList())
        whenever(eventImageRepository.findByEventIn(emptyList())).thenReturn(emptyList())

        val result = eventReader.getProcessingEventPreviews()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getProcessingEventPreviews는 최대 5개의 이벤트만 반환한다`() {
        val events = (1..10L).map { createMockEvent(it) }
        val limitedEvents = events.take(5)

        whenever(eventRepository.getByEventPreviews(any<LocalDateTime>())).thenReturn(limitedEvents)
        whenever(eventImageRepository.findByEventIn(limitedEvents)).thenReturn(emptyList())

        val result = eventReader.getProcessingEventPreviews()

        assertEquals(5, result.size)
    }
}

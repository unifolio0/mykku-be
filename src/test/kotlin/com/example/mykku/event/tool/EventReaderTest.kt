package com.example.mykku.event.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import com.example.mykku.event.domain.EventSortType
import com.example.mykku.event.domain.EventStatusType
import com.example.mykku.event.exception.EventErrorCode
import com.example.mykku.event.exception.EventException
import com.example.mykku.event.repository.EventImageRepository
import com.example.mykku.event.repository.EventRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.Optional

@DisplayName("EventReader 테스트")
class EventReaderTest : BaseToolTest() {

    @Mock
    private lateinit var eventRepository: EventRepository

    @Mock
    private lateinit var eventImageRepository: EventImageRepository

    @InjectMocks
    private lateinit var eventReader: EventReader

    @Test
    @DisplayName("ID로 이벤트를 조회한다")
    fun `ID로 이벤트를 조회한다`() {
        // given
        val event = Event(
            id = 1L,
            title = "테스트 이벤트",
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        whenever(eventRepository.findById(1L)).thenReturn(Optional.of(event))

        // when
        val result = eventReader.getEventById(1L)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 이벤트")
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 조회 시 예외를 던진다")
    fun `존재하지 않는 이벤트 조회 시 예외를 던진다`() {
        // given
        whenever(eventRepository.findById(999L)).thenReturn(Optional.empty())

        // when & then
        val exception = assertThrows<EventException> {
            eventReader.getEventById(999L)
        }
        assertThat(exception.errorCode).isEqualTo(EventErrorCode.EVENT_NOT_FOUND)
    }

    @Test
    @DisplayName("진행중인 이벤트 미리보기를 조회한다")
    fun `진행중인 이벤트 미리보기를 조회한다`() {
        // given
        val event1 = Event(id = 1L, title = "이벤트1", expiredAt = LocalDateTime.now().plusDays(7))
        val event2 = Event(id = 2L, title = "이벤트2", expiredAt = LocalDateTime.now().plusDays(7))
        val events = listOf(event1, event2)

        val image1 = EventImage(id = 1L, url = "url1", orderIndex = 0, event = event1)
        val image2 = EventImage(id = 2L, url = "url2", orderIndex = 0, event = event2)

        whenever(eventRepository.findByExpiredAtAfter(any())).thenReturn(events)
        whenever(eventImageRepository.findByEventIn(events)).thenReturn(listOf(image1, image2))

        // when
        val result = eventReader.getProcessingEventPreviews()

        // then
        assertThat(result).hasSize(2)
    }

    @Test
    @DisplayName("ACTIVE 상태와 LATEST 정렬로 이벤트를 페이지네이션 조회한다")
    fun `ACTIVE 상태와 LATEST 정렬로 이벤트를 페이지네이션 조회한다`() {
        // given
        val events = listOf(
            Event(id = 1L, title = "이벤트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findByExpiredAtAfterOrderByCreatedAtDesc(any(), any())).thenReturn(page)

        // when
        val result = eventReader.getEventsWithPagination(
            EventStatusType.ACTIVE,
            EventSortType.LATEST,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(1)
    }

    @Test
    @DisplayName("ACTIVE 상태와 OLDEST 정렬로 이벤트를 조회한다")
    fun `ACTIVE 상태와 OLDEST 정렬로 이벤트를 조회한다`() {
        // given
        val events = listOf(
            Event(id = 1L, title = "이벤트1", expiredAt = LocalDateTime.now().plusDays(7))
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findByExpiredAtAfterOrderByCreatedAtAsc(any(), any())).thenReturn(page)

        // when
        val result = eventReader.getEventsWithPagination(
            EventStatusType.ACTIVE,
            EventSortType.OLDEST,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(1)
    }

    @Test
    @DisplayName("ACTIVE 상태와 POPULAR 정렬로 이벤트를 조회한다")
    fun `ACTIVE 상태와 POPULAR 정렬로 이벤트를 조회한다`() {
        // given
        val events = listOf(
            Event(id = 1L, title = "이벤트1", expiredAt = LocalDateTime.now().plusDays(7), scrapCount = 100)
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findActiveEventsByPopular(any(), any())).thenReturn(page)

        // when
        val result = eventReader.getEventsWithPagination(
            EventStatusType.ACTIVE,
            EventSortType.POPULAR,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(1)
    }

    @Test
    @DisplayName("EXPIRED 상태로 이벤트를 조회한다")
    fun `EXPIRED 상태로 이벤트를 조회한다`() {
        // given
        val events = listOf(
            Event(id = 1L, title = "만료 이벤트", expiredAt = LocalDateTime.now().minusDays(1))
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(any(), any())).thenReturn(page)

        // when
        val result = eventReader.getEventsWithPagination(
            EventStatusType.EXPIRED,
            EventSortType.LATEST,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(1)
    }

    @Test
    @DisplayName("ALL 상태로 모든 이벤트를 조회한다")
    fun `ALL 상태로 모든 이벤트를 조회한다`() {
        // given
        val events = listOf(
            Event(id = 1L, title = "이벤트1", expiredAt = LocalDateTime.now().plusDays(7)),
            Event(id = 2L, title = "이벤트2", expiredAt = LocalDateTime.now().minusDays(1))
        )
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(events, pageable, events.size.toLong())

        whenever(eventRepository.findAllByOrderByCreatedAtDesc(any())).thenReturn(page)

        // when
        val result = eventReader.getEventsWithPagination(
            EventStatusType.ALL,
            EventSortType.LATEST,
            pageable,
            LocalDateTime.now()
        )

        // then
        assertThat(result.content).hasSize(2)
    }

    @Test
    @DisplayName("이벤트 이미지를 조회한다")
    fun `이벤트 이미지를 조회한다`() {
        // given
        val event1 = Event(id = 1L, title = "이벤트1", expiredAt = LocalDateTime.now().plusDays(7))
        val event2 = Event(id = 2L, title = "이벤트2", expiredAt = LocalDateTime.now().plusDays(7))
        val events = listOf(event1, event2)

        val images = listOf(
            EventImage(id = 1L, url = "url1", orderIndex = 0, event = event1),
            EventImage(id = 2L, url = "url2", orderIndex = 1, event = event1),
            EventImage(id = 3L, url = "url3", orderIndex = 0, event = event2)
        )

        whenever(eventImageRepository.findByEventIn(events)).thenReturn(images)

        // when
        val result = eventReader.getEventImages(events)

        // then
        assertThat(result).hasSize(2)
        assertThat(result[1L]).hasSize(2)
        assertThat(result[2L]).hasSize(1)
    }

    @Test
    @DisplayName("관계와 함께 이벤트를 조회한다")
    fun `관계와 함께 이벤트를 조회한다`() {
        // given
        val event = Event(id = 1L, title = "테스트 이벤트", expiredAt = LocalDateTime.now().plusDays(7))

        whenever(eventRepository.findById(1L)).thenReturn(Optional.of(event))

        // when
        val result = eventReader.getEventByIdWithRelations(1L)

        // then
        assertThat(result.id).isEqualTo(1L)
    }

    @Test
    @DisplayName("관계 조회 시 존재하지 않으면 예외를 던진다")
    fun `관계 조회 시 존재하지 않으면 예외를 던진다`() {
        // given
        whenever(eventRepository.findById(999L)).thenReturn(Optional.empty())

        // when & then
        val exception = assertThrows<EventException> {
            eventReader.getEventByIdWithRelations(999L)
        }
        assertThat(exception.errorCode).isEqualTo(EventErrorCode.EVENT_NOT_FOUND)
    }
}

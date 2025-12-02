package com.example.mykku.event.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import com.example.mykku.event.dto.EventImageRequest
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
import java.time.LocalDateTime

@DisplayName("EventWriter 테스트")
class EventWriterTest : BaseToolTest() {

    @Mock
    private lateinit var eventRepository: EventRepository

    @Mock
    private lateinit var eventImageRepository: EventImageRepository

    @InjectMocks
    private lateinit var eventWriter: EventWriter

    @Test
    @DisplayName("이벤트를 생성한다")
    fun `이벤트를 생성한다`() {
        // given
        val title = "테스트 이벤트"
        val description = "이벤트 설명"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val imageRequests = listOf(
            EventImageRequest(url = "url1", orderIndex = 0),
            EventImageRequest(url = "url2", orderIndex = 1)
        )

        val savedEvent = Event(id = 1L, title = title, description = description, startedAt = LocalDateTime.now(), expiredAt = expiredAt)
        val savedImages = imageRequests.mapIndexed { index, req ->
            EventImage(id = (index + 1).toLong(), url = req.url, orderIndex = req.orderIndex, event = savedEvent)
        }

        whenever(eventRepository.save(any<Event>())).thenReturn(savedEvent)
        whenever(eventImageRepository.saveAll(any<List<EventImage>>())).thenReturn(savedImages)

        // when
        val (event, images) = eventWriter.createEvent(title, description, LocalDateTime.now(), expiredAt, imageRequests)

        // then
        assertThat(event.id).isEqualTo(1L)
        assertThat(event.title).isEqualTo(title)
        assertThat(event.description).isEqualTo(description)
        assertThat(images).hasSize(2)
    }

    @Test
    @DisplayName("이미지 없이 이벤트를 생성한다")
    fun `이미지 없이 이벤트를 생성한다`() {
        // given
        val title = "테스트 이벤트"
        val expiredAt = LocalDateTime.now().plusDays(7)

        val savedEvent = Event(id = 1L, title = title, startedAt = LocalDateTime.now(), expiredAt = expiredAt)

        whenever(eventRepository.save(any<Event>())).thenReturn(savedEvent)
        whenever(eventImageRepository.saveAll(any<List<EventImage>>())).thenReturn(emptyList())

        // when
        val (event, images) = eventWriter.createEvent(title, null, LocalDateTime.now(), expiredAt, emptyList())

        // then
        assertThat(event.id).isEqualTo(1L)
        assertThat(images).isEmpty()
    }

    @Test
    @DisplayName("이미지가 10개 초과시 예외를 던진다")
    fun `이미지가 10개 초과시 예외를 던진다`() {
        // given
        val title = "테스트 이벤트"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val imageRequests = (0..10).map { EventImageRequest(url = "url$it", orderIndex = it) }

        // when & then
        val exception = assertThrows<EventException> {
            eventWriter.createEvent(title, null, LocalDateTime.now(), expiredAt, imageRequests)
        }
        assertThat(exception.errorCode).isEqualTo(EventErrorCode.EVENT_IMAGE_LIMIT_EXCEEDED)
    }

    @Test
    @DisplayName("이미지가 정확히 10개일 때 정상 생성된다")
    fun `이미지가 정확히 10개일 때 정상 생성된다`() {
        // given
        val title = "테스트 이벤트"
        val expiredAt = LocalDateTime.now().plusDays(7)
        val imageRequests = (0..9).map { EventImageRequest(url = "url$it", orderIndex = it) }

        val savedEvent = Event(id = 1L, title = title, startedAt = LocalDateTime.now(), expiredAt = expiredAt)
        val savedImages = imageRequests.mapIndexed { index, req ->
            EventImage(id = (index + 1).toLong(), url = req.url, orderIndex = req.orderIndex, event = savedEvent)
        }

        whenever(eventRepository.save(any<Event>())).thenReturn(savedEvent)
        whenever(eventImageRepository.saveAll(any<List<EventImage>>())).thenReturn(savedImages)

        // when
        val (event, images) = eventWriter.createEvent(title, null, LocalDateTime.now(), expiredAt, imageRequests)

        // then
        assertThat(event.id).isEqualTo(1L)
        assertThat(images).hasSize(10)
    }
}

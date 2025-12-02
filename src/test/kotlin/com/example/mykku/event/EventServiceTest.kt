package com.example.mykku.event

import com.example.mykku.BaseServiceTest
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import com.example.mykku.event.domain.EventSortType
import com.example.mykku.event.domain.EventStatusType
import com.example.mykku.event.dto.CreateEventRequest
import com.example.mykku.event.dto.EventImageRequest
import com.example.mykku.event.dto.EventListResponse
import com.example.mykku.event.dto.EventDetailResponse
import com.example.mykku.event.dto.EventImageResponse
import com.example.mykku.event.tool.EventDtoConverter
import com.example.mykku.event.tool.EventParticipationReader
import com.example.mykku.event.tool.EventReader
import com.example.mykku.event.tool.EventWriter
import com.example.mykku.scrap.tool.SaveEventReader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("EventService 테스트")
class EventServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var eventWriter: EventWriter

    @Mock
    private lateinit var eventReader: EventReader

    @Mock
    private lateinit var eventParticipationReader: EventParticipationReader

    @Mock
    private lateinit var saveEventReader: SaveEventReader

    @Mock
    private lateinit var eventDtoConverter: EventDtoConverter

    @InjectMocks
    private lateinit var eventService: EventService

    @Test
    @DisplayName("이벤트를 생성한다")
    fun `이벤트를 생성한다`() {
        // given
        val request = CreateEventRequest(
            title = "테스트 이벤트",
            description = "이벤트 설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7),
            images = listOf(
                EventImageRequest(url = "url1", orderIndex = 0),
                EventImageRequest(url = "url2", orderIndex = 1)
            )
        )

        val event = Event(id = 1L, title = request.title, description = request.description, startedAt = LocalDateTime.now(), expiredAt = request.expiredAt)
        initializeBaseEntityFields(event, LocalDateTime.now())

        val images = listOf(
            EventImage(id = 1L, url = "url1", orderIndex = 0, event = event),
            EventImage(id = 2L, url = "url2", orderIndex = 1, event = event)
        )

        whenever(eventWriter.createEvent(any(), anyOrNull(), any(), any(), any())).thenReturn(Pair(event, images))

        // when
        val result = eventService.createEvent(request)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 이벤트")
        assertThat(result.description).isEqualTo("이벤트 설명")
        assertThat(result.images).hasSize(2)
    }

    @Test
    @DisplayName("이미지 없이 이벤트를 생성한다")
    fun `이미지 없이 이벤트를 생성한다`() {
        // given
        val request = CreateEventRequest(
            title = "테스트 이벤트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        val event = Event(id = 1L, title = request.title, startedAt = LocalDateTime.now(), expiredAt = request.expiredAt)
        initializeBaseEntityFields(event, LocalDateTime.now())

        whenever(eventWriter.createEvent(any(), anyOrNull(), any(), any(), any())).thenReturn(Pair(event, emptyList()))

        // when
        val result = eventService.createEvent(request)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.images).isEmpty()
    }

    @Test
    @DisplayName("이벤트 목록을 조회한다")
    fun `이벤트 목록을 조회한다`() {
        // given
        val member = createTestMember()
        val event1 = Event(id = 1L, title = "이벤트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val event2 = Event(id = 2L, title = "이벤트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val events = listOf(event1, event2)
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(events, pageable, events.size.toLong())

        val image1 = EventImage(id = 1L, url = "url1", orderIndex = 0, event = event1)
        val imagesByEventId = mapOf(1L to listOf(image1), 2L to emptyList())

        val response1 = EventListResponse(id = 1L, title = "이벤트1", startedAt = event1.startedAt, expiredAt = event1.expiredAt, status = event1.status, thumbnailUrl = "url1", isSaved = true)
        val response2 = EventListResponse(id = 2L, title = "이벤트2", startedAt = event2.startedAt, expiredAt = event2.expiredAt, status = event2.status, thumbnailUrl = null, isSaved = false)

        whenever(eventReader.getEventsWithPagination(any(), any(), any(), any())).thenReturn(page)
        whenever(eventReader.getEventImages(events)).thenReturn(imagesByEventId)
        whenever(saveEventReader.getSavedEventIds(eq(member), any())).thenReturn(setOf(1L))
        whenever(eventDtoConverter.toEventListResponse(eq(event1), any(), eq(true))).thenReturn(response1)
        whenever(eventDtoConverter.toEventListResponse(eq(event2), any(), eq(false))).thenReturn(response2)

        // when
        val result = eventService.getEvents(EventStatusType.ACTIVE, EventSortType.LATEST, 0, 20, member)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2)
    }

    @Test
    @DisplayName("이벤트 상세를 조회한다")
    fun `이벤트 상세를 조회한다`() {
        // given
        val member = createTestMember()
        val event = Event(id = 1L, title = "테스트 이벤트", description = "설명", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        initializeBaseEntityFields(event, LocalDateTime.now())

        val images = listOf(
            EventImage(id = 1L, url = "url1", orderIndex = 0, event = event)
        )
        val imagesByEventId = mapOf(1L to images)

        val expectedResponse = EventDetailResponse(
            id = 1L,
            title = "테스트 이벤트",
            description = "설명",
            startedAt = event.startedAt,
            expiredAt = event.expiredAt,
            status = event.status,
            images = listOf(EventImageResponse(url = "url1", orderIndex = 0)),
            isSaved = true,
            createdAt = event.createdAt
        )

        whenever(eventReader.getEventByIdWithRelations(1L)).thenReturn(event)
        whenever(eventReader.getEventImages(listOf(event))).thenReturn(imagesByEventId)
        whenever(saveEventReader.isSaved(member, event)).thenReturn(true)
        whenever(eventDtoConverter.toEventDetailResponse(event, images, true)).thenReturn(expectedResponse)

        // when
        val result = eventService.getEventDetail(1L, member)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 이벤트")
        assertThat(result.isSaved).isTrue()
    }

    @Test
    @DisplayName("저장하지 않은 이벤트 상세를 조회한다")
    fun `저장하지 않은 이벤트 상세를 조회한다`() {
        // given
        val member = createTestMember()
        val event = Event(id = 1L, title = "테스트 이벤트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        initializeBaseEntityFields(event, LocalDateTime.now())

        val imagesByEventId = mapOf(1L to emptyList<EventImage>())

        val expectedResponse = EventDetailResponse(
            id = 1L,
            title = "테스트 이벤트",
            description = null,
            startedAt = event.startedAt,
            expiredAt = event.expiredAt,
            status = event.status,
            images = emptyList(),
            isSaved = false,
            createdAt = event.createdAt
        )

        whenever(eventReader.getEventByIdWithRelations(1L)).thenReturn(event)
        whenever(eventReader.getEventImages(listOf(event))).thenReturn(imagesByEventId)
        whenever(saveEventReader.isSaved(member, event)).thenReturn(false)
        whenever(eventDtoConverter.toEventDetailResponse(eq(event), any(), eq(false))).thenReturn(expectedResponse)

        // when
        val result = eventService.getEventDetail(1L, member)

        // then
        assertThat(result.isSaved).isFalse()
    }

    @Test
    @DisplayName("내가 참여한 이벤트 목록을 조회한다")
    fun `내가 참여한 이벤트 목록을 조회한다`() {
        // given
        val member = createTestMember()
        val event1 = Event(id = 1L, title = "이벤트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val event2 = Event(id = 2L, title = "이벤트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val events = listOf(event1, event2)
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(events, pageable, events.size.toLong())

        val image1 = EventImage(id = 1L, url = "url1", orderIndex = 0, event = event1)
        val imagesByEventId = mapOf(1L to listOf(image1), 2L to emptyList<EventImage>())

        val response1 = EventListResponse(id = 1L, title = "이벤트1", startedAt = event1.startedAt, expiredAt = event1.expiredAt, status = event1.status, thumbnailUrl = "url1", isSaved = true)
        val response2 = EventListResponse(id = 2L, title = "이벤트2", startedAt = event2.startedAt, expiredAt = event2.expiredAt, status = event2.status, thumbnailUrl = null, isSaved = false)

        whenever(eventParticipationReader.getParticipatedEvents(eq(member), any())).thenReturn(page)
        whenever(eventReader.getEventImages(events)).thenReturn(imagesByEventId)
        whenever(saveEventReader.getSavedEventIds(eq(member), any())).thenReturn(setOf(1L))
        whenever(eventDtoConverter.toEventListResponse(eq(event1), any(), eq(true))).thenReturn(response1)
        whenever(eventDtoConverter.toEventListResponse(eq(event2), any(), eq(false))).thenReturn(response2)

        // when
        val result = eventService.getMyParticipatedEvents(member, 0, 20)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2)
    }
}

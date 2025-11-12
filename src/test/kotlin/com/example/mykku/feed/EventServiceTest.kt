package com.example.mykku.feed

import com.example.mykku.BaseServiceTest
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage
import com.example.mykku.feed.domain.EventTag
import com.example.mykku.feed.dto.CreateEventRequest
import com.example.mykku.feed.dto.EventImageRequest
import com.example.mykku.feed.tool.EventWriter
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.test.assertEquals

import com.example.mykku.feed.domain.EventSortType
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.tool.EventDtoConverter
import com.example.mykku.feed.tool.EventReader
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.tool.SaveEventReader
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class EventServiceTest : BaseServiceTest() {

    @Mock
    private lateinit var eventWriter: EventWriter

    @Mock
    private lateinit var eventReader: EventReader

    @Mock
    private lateinit var saveEventReader: SaveEventReader

    @Mock
    private lateinit var eventDtoConverter: EventDtoConverter

    @InjectMocks
    private lateinit var eventService: EventService
    
    private fun createTestEvent(
        id: Long = 1L,
        title: String,
        isContest: Boolean,
        expiredAt: LocalDateTime
    ): Event {
        val event = Event(
            id = id,
            title = title,
            isContest = isContest,
            expiredAt = expiredAt
        )
        initializeBaseEntityFieldsFromSuperclass(event)
        return event
    }

    @Test
    fun `createEvent - 이벤트를 정상적으로 생성한다`() {
        // given
        val expiredAt = LocalDateTime.now().plusDays(7)
        val imageRequest = EventImageRequest(
            url = "https://s3.amazonaws.com/event.jpg",
            orderIndex = 0
        )
        val request = CreateEventRequest(
            title = "새 이벤트",
            isContest = true,
            expiredAt = expiredAt,
            images = listOf(imageRequest),
            tags = listOf("이벤트", "콘테스트")
        )

        val event = createTestEvent(
            title = request.title,
            isContest = request.isContest,
            expiredAt = request.expiredAt
        )

        val eventImage = EventImage(
            url = imageRequest.url,
            orderIndex = imageRequest.orderIndex,
            event = event
        )

        val eventTag1 = EventTag(title = "이벤트", event = event)
        val eventTag2 = EventTag(title = "콘테스트", event = event)

        whenever(eventWriter.createEvent(
            title = request.title,
            isContest = request.isContest,
            expiredAt = request.expiredAt,
            imageRequests = request.images,
            tagTitles = request.tags
        )).thenReturn(Triple(event, listOf(eventImage), listOf(eventTag1, eventTag2)))

        // when
        val result = eventService.createEvent(request)

        // then
        assertEquals(event.id, result.id)
        assertEquals(event.title, result.title)
        assertEquals(event.isContest, result.isContest)
        assertEquals(event.expiredAt, result.expiredAt)
        assertEquals(1, result.images.size)
        assertEquals(imageRequest.url, result.images[0].url)
        assertEquals(imageRequest.orderIndex, result.images[0].orderIndex)
        assertEquals(2, result.tags.size)
        assertEquals(setOf("이벤트", "콘테스트"), result.tags.toSet())
    }

    @Test
    fun `createEvent - 이미지가 여러 개인 경우 순서대로 정렬된다`() {
        // given
        val expiredAt = LocalDateTime.now().plusDays(7)
        val imageRequest1 = EventImageRequest(url = "image1.jpg", orderIndex = 2)
        val imageRequest2 = EventImageRequest(url = "image2.jpg", orderIndex = 0)
        val imageRequest3 = EventImageRequest(url = "image3.jpg", orderIndex = 1)
        
        val request = CreateEventRequest(
            title = "새 이벤트",
            isContest = false,
            expiredAt = expiredAt,
            images = listOf(imageRequest1, imageRequest2, imageRequest3),
            tags = emptyList()
        )

        val event = createTestEvent(
            title = request.title,
            isContest = request.isContest,
            expiredAt = request.expiredAt
        )

        val eventImage1 = EventImage(url = imageRequest1.url, orderIndex = imageRequest1.orderIndex, event = event)
        val eventImage2 = EventImage(url = imageRequest2.url, orderIndex = imageRequest2.orderIndex, event = event)
        val eventImage3 = EventImage(url = imageRequest3.url, orderIndex = imageRequest3.orderIndex, event = event)

        whenever(eventWriter.createEvent(
            title = request.title,
            isContest = request.isContest,
            expiredAt = request.expiredAt,
            imageRequests = request.images,
            tagTitles = request.tags
        )).thenReturn(Triple(event, listOf(eventImage1, eventImage2, eventImage3), emptyList()))

        // when
        val result = eventService.createEvent(request)

        // then
        assertEquals(3, result.images.size)
        assertEquals("image2.jpg", result.images[0].url) // orderIndex 0
        assertEquals("image3.jpg", result.images[1].url) // orderIndex 1
        assertEquals("image1.jpg", result.images[2].url) // orderIndex 2
    }

    @Test
    fun `createEvent - 이미지와 태그가 없는 이벤트를 생성한다`() {
        // given
        val expiredAt = LocalDateTime.now().plusDays(7)
        val request = CreateEventRequest(
            title = "간단한 이벤트",
            isContest = false,
            expiredAt = expiredAt,
            images = emptyList(),
            tags = emptyList()
        )

        val event = createTestEvent(
            title = request.title,
            isContest = request.isContest,
            expiredAt = request.expiredAt
        )

        whenever(eventWriter.createEvent(
            title = request.title,
            isContest = request.isContest,
            expiredAt = request.expiredAt,
            imageRequests = request.images,
            tagTitles = request.tags
        )).thenReturn(Triple(event, emptyList(), emptyList()))

        // when
        val result = eventService.createEvent(request)

        // then
        assertEquals(event.id, result.id)
        assertEquals(event.title, result.title)
        assertEquals(0, result.images.size)
        assertEquals(0, result.tags.size)
    }


    @Test
    fun `getEvents - 이벤트 목록을 정상적으로 조회한다`() {
        val member = Member(id = 1L, email = "test@test.com", nickname = "테스터")
        val event = createTestEvent(title = "이벤트", isContest = false, expiredAt = LocalDateTime.now().plusDays(7))
        val pageable = PageRequest.of(0, 20)
        val eventPage = PageImpl(listOf(event), pageable, 1)

        whenever(eventReader.getEventsWithPagination(eq("active"), eq(EventSortType.LATEST), any(), any())).thenReturn(eventPage)
        whenever(eventReader.getEventImages(any())).thenReturn(emptyMap())
        whenever(eventReader.getEventTags(any())).thenReturn(emptyMap())
        whenever(saveEventReader.isSaved(any(), any())).thenReturn(false)
        whenever(eventDtoConverter.toEventListResponse(any(), any(), any(), any())).thenCallRealMethod()

        val result = eventService.getEvents("active", EventSortType.LATEST, 0, 20, member)

        assertEquals(1, result.totalElements)
    }

    @Test
    fun `getEventDetail - 이벤트 상세를 정상적으로 조회한다`() {
        val member = Member(id = 1L, email = "test@test.com", nickname = "테스터")
        val event = createTestEvent(title = "이벤트", isContest = false, expiredAt = LocalDateTime.now().plusDays(7))

        whenever(eventReader.getEventByIdWithRelations(1L)).thenReturn(event)
        whenever(eventReader.getEventImages(any())).thenReturn(emptyMap())
        whenever(eventReader.getEventTags(any())).thenReturn(emptyMap())
        whenever(saveEventReader.isSaved(any(), any())).thenReturn(true)
        whenever(eventDtoConverter.toEventDetailResponse(any(), any(), any(), any())).thenCallRealMethod()

        val result = eventService.getEventDetail(1L, member)

        assertEquals(1L, result.id)
        assertEquals(true, result.isSaved)
    }

    @Test
    fun `getEventDetail - 존재하지 않는 이벤트 조회 시 예외를 발생시킨다`() {
        val member = Member(id = 1L, email = "test@test.com", nickname = "테스터")

        whenever(eventReader.getEventByIdWithRelations(999L)).thenThrow(FeedException.eventNotFound())

        assertThrows<FeedException> {
            eventService.getEventDetail(999L, member)
        }
    }
}
package com.example.mykku.feed.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage
import com.example.mykku.feed.domain.EventTag
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EventDtoConverterTest : BaseToolTest() {

    @InjectMocks
    private lateinit var eventDtoConverter: EventDtoConverter

    private fun createMockEvent(id: Long): Event {
        val event = Event(
            id = id,
            isContest = false,
            title = "테스트 이벤트 $id",
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val createdAtField = event.javaClass.superclass.getDeclaredField("createdAt")
        createdAtField.isAccessible = true
        createdAtField.set(event, LocalDateTime.now())
        return event
    }

    @Test
    fun `toEventListResponse는 이벤트를 EventListResponse로 변환한다`() {
        val event = createMockEvent(1L)
        val image1 = EventImage(id = 1L, url = "url1", orderIndex = 1, event = event)
        val image2 = EventImage(id = 2L, url = "url2", orderIndex = 0, event = event)
        val tag = EventTag(id = 1L, title = "tag1", event = event)

        val result = eventDtoConverter.toEventListResponse(
            event = event,
            images = listOf(image1, image2),
            tags = listOf(tag),
            isSaved = true
        )

        assertEquals(1L, result.id)
        assertEquals("테스트 이벤트 1", result.title)
        assertEquals(false, result.isContest)
        assertEquals("url2", result.thumbnailUrl)
        assertEquals(listOf("tag1"), result.tags)
        assertTrue(result.isSaved)
    }

    @Test
    fun `toEventListResponse는 이미지가 없을 때 null 썸네일을 반환한다`() {
        val event = createMockEvent(1L)

        val result = eventDtoConverter.toEventListResponse(
            event = event,
            images = emptyList(),
            tags = emptyList(),
            isSaved = false
        )

        assertNull(result.thumbnailUrl)
    }

    @Test
    fun `toEventListResponse는 이미지를 orderIndex 순으로 정렬하여 썸네일을 선택한다`() {
        val event = createMockEvent(1L)
        val image1 = EventImage(id = 1L, url = "url1", orderIndex = 2, event = event)
        val image2 = EventImage(id = 2L, url = "url2", orderIndex = 0, event = event)
        val image3 = EventImage(id = 3L, url = "url3", orderIndex = 1, event = event)

        val result = eventDtoConverter.toEventListResponse(
            event = event,
            images = listOf(image1, image2, image3),
            tags = emptyList(),
            isSaved = false
        )

        assertEquals("url2", result.thumbnailUrl)
    }

    @Test
    fun `toEventDetailResponse는 이벤트를 EventDetailResponse로 변환한다`() {
        val event = createMockEvent(1L)
        val image1 = EventImage(id = 1L, url = "url1", orderIndex = 1, event = event)
        val image2 = EventImage(id = 2L, url = "url2", orderIndex = 0, event = event)
        val tag = EventTag(id = 1L, title = "tag1", event = event)

        val result = eventDtoConverter.toEventDetailResponse(
            event = event,
            images = listOf(image1, image2),
            tags = listOf(tag),
            isSaved = true
        )

        assertEquals(1L, result.id)
        assertEquals("테스트 이벤트 1", result.title)
        assertEquals(false, result.isContest)
        assertEquals(2, result.images.size)
        assertEquals("url2", result.images[0].url)
        assertEquals(0, result.images[0].orderIndex)
        assertEquals("url1", result.images[1].url)
        assertEquals(1, result.images[1].orderIndex)
        assertEquals(listOf("tag1"), result.tags)
        assertTrue(result.isSaved)
    }

    @Test
    fun `toEventDetailResponse는 빈 이미지와 태그로 변환할 수 있다`() {
        val event = createMockEvent(1L)

        val result = eventDtoConverter.toEventDetailResponse(
            event = event,
            images = emptyList(),
            tags = emptyList(),
            isSaved = false
        )

        assertTrue(result.images.isEmpty())
        assertTrue(result.tags.isEmpty())
    }
}

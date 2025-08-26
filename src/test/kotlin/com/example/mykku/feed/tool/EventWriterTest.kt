package com.example.mykku.feed.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.domain.EventImage
import com.example.mykku.feed.domain.EventTag
import com.example.mykku.feed.dto.EventImageRequest
import com.example.mykku.feed.repository.EventImageRepository
import com.example.mykku.feed.repository.EventRepository
import com.example.mykku.feed.repository.EventTagRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
class EventWriterTest {

    @Mock
    private lateinit var eventRepository: EventRepository

    @Mock
    private lateinit var eventImageRepository: EventImageRepository

    @Mock
    private lateinit var eventTagRepository: EventTagRepository

    @InjectMocks
    private lateinit var eventWriter: EventWriter

    @Test
    fun `createEvent는 이벤트, 이미지, 태그를 생성하고 저장한다`() {
        val title = "테스트 이벤트"
        val isContest = true
        val expiredAt = LocalDateTime.now().plusDays(30)
        val imageRequests = listOf(
            EventImageRequest(url = "image1.jpg", orderIndex = 1),
            EventImageRequest(url = "image2.jpg", orderIndex = 2)
        )
        val tagTitles = listOf("태그1", "태그2")
        
        val mockEvent = Event(
            id = 1L,
            title = title,
            isContest = isContest,
            expiredAt = expiredAt
        )
        
        whenever(eventRepository.save(any<Event>())).thenReturn(mockEvent)
        whenever(eventImageRepository.saveAll(any<List<EventImage>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }
        whenever(eventTagRepository.saveAll(any<List<EventTag>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }

        val result = eventWriter.createEvent(title, isContest, expiredAt, imageRequests, tagTitles)

        assertEquals(mockEvent, result.first)
        assertEquals(2, result.second.size)
        assertEquals(2, result.third.size)
        assertTrue(result.first.isContest)
    }

    @Test
    fun `createEvent는 콘테스트가 아닌 이벤트도 생성할 수 있다`() {
        val title = "일반 이벤트"
        val isContest = false
        val expiredAt = LocalDateTime.now().plusDays(15)
        val imageRequests = emptyList<EventImageRequest>()
        val tagTitles = listOf("이벤트")
        
        val mockEvent = Event(
            id = 2L,
            title = title,
            isContest = isContest,
            expiredAt = expiredAt
        )
        
        whenever(eventRepository.save(any<Event>())).thenReturn(mockEvent)
        whenever(eventImageRepository.saveAll(any<List<EventImage>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }
        whenever(eventTagRepository.saveAll(any<List<EventTag>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }

        val result = eventWriter.createEvent(title, isContest, expiredAt, imageRequests, tagTitles)

        assertEquals(mockEvent, result.first)
        assertEquals(false, result.first.isContest)
    }

    @Test
    fun `createEvent는 이미지 개수가 최대치를 초과하면 예외를 발생시킨다`() {
        val title = "테스트 이벤트"
        val isContest = true
        val expiredAt = LocalDateTime.now().plusDays(30)
        val imageRequests = List(Event.IMAGE_MAX_COUNT + 1) { index ->
            EventImageRequest(url = "image$index.jpg", orderIndex = index)
        }
        val tagTitles = listOf("태그1")

        val exception = assertThrows<MykkuException> {
            eventWriter.createEvent(title, isContest, expiredAt, imageRequests, tagTitles)
        }

        assertEquals(ErrorCode.EVENT_IMAGE_LIMIT_EXCEEDED, exception.errorCode)
    }

    @Test
    fun `createEvent는 태그 개수가 최대치를 초과하면 예외를 발생시킨다`() {
        val title = "테스트 이벤트"
        val isContest = false
        val expiredAt = LocalDateTime.now().plusDays(30)
        val imageRequests = emptyList<EventImageRequest>()
        val tagTitles = List(Event.TAG_MAX_COUNT + 1) { "태그$it" }

        val exception = assertThrows<MykkuException> {
            eventWriter.createEvent(title, isContest, expiredAt, imageRequests, tagTitles)
        }

        assertEquals(ErrorCode.EVENT_TAG_LIMIT_EXCEEDED, exception.errorCode)
    }

    @Test
    fun `createEvent는 중복된 태그를 제거하고 저장한다`() {
        val title = "테스트 이벤트"
        val isContest = false
        val expiredAt = LocalDateTime.now().plusDays(30)
        val imageRequests = emptyList<EventImageRequest>()
        val tagTitles = listOf("태그1", "태그1", "태그2", " 태그2 ", "", "  ")
        
        val mockEvent = Event(
            id = 3L,
            title = title,
            isContest = isContest,
            expiredAt = expiredAt
        )
        
        whenever(eventRepository.save(any<Event>())).thenReturn(mockEvent)
        whenever(eventImageRepository.saveAll(any<List<EventImage>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }
        whenever(eventTagRepository.saveAll(any<List<EventTag>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }

        val result = eventWriter.createEvent(title, isContest, expiredAt, imageRequests, tagTitles)

        assertEquals(2, result.third.size) // "태그1", "태그2"만 저장
        val savedTitles = result.third.map { it.title }
        assertTrue(savedTitles.contains("태그1"))
        assertTrue(savedTitles.contains("태그2"))
    }

    @Test
    fun `createEvent는 빈 이미지와 태그 리스트로도 이벤트를 생성할 수 있다`() {
        val title = "최소 이벤트"
        val isContest = true
        val expiredAt = LocalDateTime.now().plusHours(1)
        val imageRequests = emptyList<EventImageRequest>()
        val tagTitles = emptyList<String>()
        
        val mockEvent = Event(
            id = 4L,
            title = title,
            isContest = isContest,
            expiredAt = expiredAt
        )
        
        whenever(eventRepository.save(any<Event>())).thenReturn(mockEvent)
        whenever(eventImageRepository.saveAll(any<List<EventImage>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }
        whenever(eventTagRepository.saveAll(any<List<EventTag>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }

        val result = eventWriter.createEvent(title, isContest, expiredAt, imageRequests, tagTitles)

        assertEquals(mockEvent, result.first)
        assertEquals(0, result.second.size)
        assertEquals(0, result.third.size)
    }

    @Test
    fun `createEvent는 이미지 순서를 유지하면서 저장한다`() {
        val title = "순서 테스트 이벤트"
        val isContest = false
        val expiredAt = LocalDateTime.now().plusDays(7)
        val imageRequests = listOf(
            EventImageRequest(url = "first.jpg", orderIndex = 1),
            EventImageRequest(url = "second.jpg", orderIndex = 2),
            EventImageRequest(url = "third.jpg", orderIndex = 3)
        )
        val tagTitles = emptyList<String>()
        
        val mockEvent = Event(
            id = 5L,
            title = title,
            isContest = isContest,
            expiredAt = expiredAt
        )
        
        whenever(eventRepository.save(any<Event>())).thenReturn(mockEvent)
        whenever(eventImageRepository.saveAll(any<List<EventImage>>()))
            .thenAnswer { invocation -> 
                val images = invocation.arguments[0] as List<EventImage>
                images.mapIndexed { index, image ->
                    EventImage(
                        id = (index + 1).toLong(),
                        url = image.url,
                        orderIndex = image.orderIndex,
                        event = image.event
                    )
                }
            }
        whenever(eventTagRepository.saveAll(any<List<EventTag>>()))
            .thenAnswer { invocation -> invocation.arguments[0] }

        val result = eventWriter.createEvent(title, isContest, expiredAt, imageRequests, tagTitles)

        assertEquals(3, result.second.size)
        assertEquals("first.jpg", result.second[0].url)
        assertEquals(1, result.second[0].orderIndex)
        assertEquals("second.jpg", result.second[1].url)
        assertEquals(2, result.second[1].orderIndex)
        assertEquals("third.jpg", result.second[2].url)
        assertEquals(3, result.second[2].orderIndex)
    }
}
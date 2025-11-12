package com.example.mykku.feed.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.feed.domain.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import com.example.mykku.feed.domain.EventSortType
import org.springframework.data.domain.PageRequest

class EventRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Test
    fun `findByExpiredAtAfter는 지정한 시간보다 이후에 만료되는 이벤트들을 조회한다`() {
        val currentTime = LocalDateTime.now()

        val activeEvent1 = Event(
            isContest = false,
            title = "활성 이벤트1",
            expiredAt = currentTime.plusDays(10)
        )
        eventRepository.save(activeEvent1)

        val activeEvent2 = Event(
            isContest = true,
            title = "활성 콘테스트",
            expiredAt = currentTime.plusHours(1)
        )
        eventRepository.save(activeEvent2)

        val expiredEvent = Event(
            isContest = false,
            title = "만료된 이벤트",
            expiredAt = currentTime.minusDays(1)
        )
        eventRepository.save(expiredEvent)

        val result = eventRepository.findByExpiredAtAfter(currentTime)

        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "활성 이벤트1" })
        assertTrue(result.any { it.title == "활성 콘테스트" })
        assertTrue(result.none { it.title == "만료된 이벤트" })
    }

    @Test
    fun `findByExpiredAtAfter는 만료 시간이 정확히 같은 시간의 이벤트는 제외한다`() {
        val currentTime = LocalDateTime.now().withNano(0)

        val exactTimeEvent = Event(
            isContest = false,
            title = "정확히 같은 시간 이벤트",
            expiredAt = currentTime
        )
        eventRepository.save(exactTimeEvent)

        val futureEvent = Event(
            isContest = false,
            title = "미래 이벤트",
            expiredAt = currentTime.plusSeconds(1)
        )
        eventRepository.save(futureEvent)

        val result = eventRepository.findByExpiredAtAfter(currentTime)

        assertEquals(1, result.size)
        assertEquals("미래 이벤트", result[0].title)
    }

    @Test
    fun `findByExpiredAtAfter는 활성 이벤트가 없으면 빈 리스트를 반환한다`() {
        val currentTime = LocalDateTime.now()

        val expiredEvent1 = Event(
            isContest = false,
            title = "만료된 이벤트1",
            expiredAt = currentTime.minusDays(1)
        )
        eventRepository.save(expiredEvent1)

        val expiredEvent2 = Event(
            isContest = true,
            title = "만료된 콘테스트",
            expiredAt = currentTime.minusHours(1)
        )
        eventRepository.save(expiredEvent2)

        val result = eventRepository.findByExpiredAtAfter(currentTime)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `findByExpiredAtAfterOrderByCreatedAtDesc는 활성 이벤트를 최신순으로 정렬하여 조회한다`() {
        val currentTime = LocalDateTime.now()
        val pageable = PageRequest.of(0, 10)

        val oldEvent = Event(
            isContest = false,
            title = "오래된 이벤트",
            expiredAt = currentTime.plusDays(10)
        )
        eventRepository.save(oldEvent)
        Thread.sleep(10)

        val newEvent = Event(
            isContest = false,
            title = "최신 이벤트",
            expiredAt = currentTime.plusDays(10)
        )
        eventRepository.save(newEvent)

        val expiredEvent = Event(
            isContest = false,
            title = "만료된 이벤트",
            expiredAt = currentTime.minusDays(1)
        )
        eventRepository.save(expiredEvent)

        val result = eventRepository.findByExpiredAtAfterOrderByCreatedAtDesc(currentTime, pageable)

        assertEquals(2, result.totalElements)
        assertEquals("최신 이벤트", result.content[0].title)
        assertEquals("오래된 이벤트", result.content[1].title)
    }

    @Test
    fun `findByExpiredAtAfterOrderByCreatedAtAsc는 활성 이벤트를 오래된순으로 정렬하여 조회한다`() {
        val currentTime = LocalDateTime.now()
        val pageable = PageRequest.of(0, 10)

        val oldEvent = Event(
            isContest = false,
            title = "오래된 이벤트",
            expiredAt = currentTime.plusDays(10)
        )
        eventRepository.save(oldEvent)
        Thread.sleep(10)

        val newEvent = Event(
            isContest = false,
            title = "최신 이벤트",
            expiredAt = currentTime.plusDays(10)
        )
        eventRepository.save(newEvent)

        val result = eventRepository.findByExpiredAtAfterOrderByCreatedAtAsc(currentTime, pageable)

        assertEquals(2, result.totalElements)
        assertEquals("오래된 이벤트", result.content[0].title)
        assertEquals("최신 이벤트", result.content[1].title)
    }

    @Test
    fun `findActiveEventsByPopular는 활성 이벤트를 인기순으로 정렬하여 조회한다`() {
        val currentTime = LocalDateTime.now()
        val pageable = PageRequest.of(0, 10)

        val popularEvent = Event(
            isContest = false,
            title = "인기 이벤트",
            expiredAt = currentTime.plusDays(10),
            scrapCount = 100
        )
        eventRepository.save(popularEvent)

        val normalEvent = Event(
            isContest = false,
            title = "일반 이벤트",
            expiredAt = currentTime.plusDays(10),
            scrapCount = 10
        )
        eventRepository.save(normalEvent)

        val unpopularEvent = Event(
            isContest = false,
            title = "비인기 이벤트",
            expiredAt = currentTime.plusDays(10),
            scrapCount = 0
        )
        eventRepository.save(unpopularEvent)

        val result = eventRepository.findActiveEventsByPopular(currentTime, pageable)

        assertEquals(3, result.totalElements)
        assertEquals("인기 이벤트", result.content[0].title)
        assertEquals("일반 이벤트", result.content[1].title)
        assertEquals("비인기 이벤트", result.content[2].title)
    }

    @Test
    fun `findByExpiredAtLessThanEqualOrderByCreatedAtDesc은 만료된 이벤트만 조회한다`() {
        val currentTime = LocalDateTime.now()
        val pageable = PageRequest.of(0, 10)

        val expiredEvent1 = Event(
            isContest = false,
            title = "만료된 이벤트1",
            expiredAt = currentTime.minusDays(1)
        )
        eventRepository.save(expiredEvent1)

        val expiredEvent2 = Event(
            isContest = false,
            title = "만료된 이벤트2",
            expiredAt = currentTime.minusHours(1)
        )
        eventRepository.save(expiredEvent2)

        val activeEvent = Event(
            isContest = false,
            title = "활성 이벤트",
            expiredAt = currentTime.plusDays(1)
        )
        eventRepository.save(activeEvent)

        val result = eventRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(currentTime, pageable)

        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.title == "만료된 이벤트1" })
        assertTrue(result.content.any { it.title == "만료된 이벤트2" })
        assertTrue(result.content.none { it.title == "활성 이벤트" })
    }

    @Test
    fun `findAllByOrderByCreatedAtDesc은 모든 이벤트를 조회한다`() {
        val currentTime = LocalDateTime.now()
        val pageable = PageRequest.of(0, 10)

        val activeEvent = Event(
            isContest = false,
            title = "활성 이벤트",
            expiredAt = currentTime.plusDays(1)
        )
        eventRepository.save(activeEvent)

        val expiredEvent = Event(
            isContest = false,
            title = "만료된 이벤트",
            expiredAt = currentTime.minusDays(1)
        )
        eventRepository.save(expiredEvent)

        val result = eventRepository.findAllByOrderByCreatedAtDesc(pageable)

        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.title == "활성 이벤트" })
        assertTrue(result.content.any { it.title == "만료된 이벤트" })
    }
}

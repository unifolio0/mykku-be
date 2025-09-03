package com.example.mykku.feed.repository

import com.example.mykku.feed.domain.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Test
    fun `getByEventPreviews는 지정한 시간보다 이후에 만료되는 이벤트들을 조회한다`() {
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

        val result = eventRepository.getByEventPreviews(currentTime)

        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "활성 이벤트1" })
        assertTrue(result.any { it.title == "활성 콘테스트" })
        assertTrue(result.none { it.title == "만료된 이벤트" })
    }

    @Test
    fun `getByEventPreviews는 만료 시간이 정확히 같은 시간의 이벤트는 제외한다`() {
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

        val result = eventRepository.getByEventPreviews(currentTime)

        assertEquals(1, result.size)
        assertEquals("미래 이벤트", result[0].title)
    }

    @Test
    fun `getByEventPreviews는 활성 이벤트가 없으면 빈 리스트를 반환한다`() {
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

        val result = eventRepository.getByEventPreviews(currentTime)

        assertTrue(result.isEmpty())
    }
}
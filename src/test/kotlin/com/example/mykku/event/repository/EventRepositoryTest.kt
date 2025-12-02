package com.example.mykku.event.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.event.domain.Event
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("EventRepository 테스트")
class EventRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("이벤트를 저장하고 조회한다")
    fun `이벤트를 저장하고 조회한다`() {
        // given
        val event = Event(
            title = "테스트 이벤트",
            description = "이벤트 설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        // when
        val savedEvent = eventRepository.save(event)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundEvent = eventRepository.findById(savedEvent.id!!).orElse(null)

        // then
        assertThat(foundEvent).isNotNull
        assertThat(foundEvent.title).isEqualTo("테스트 이벤트")
        assertThat(foundEvent.description).isEqualTo("이벤트 설명")
    }

    @Test
    @DisplayName("만료되지 않은 이벤트 목록을 조회한다")
    fun `만료되지 않은 이벤트 목록을 조회한다`() {
        // given
        val activeEvent = eventRepository.save(
            Event(title = "활성 이벤트", startedAt = LocalDateTime.now().minusDays(1), expiredAt = LocalDateTime.now().plusDays(7))
        )
        val expiredEvent = eventRepository.save(
            Event(title = "만료 이벤트", startedAt = LocalDateTime.now().minusDays(2), expiredAt = LocalDateTime.now().minusDays(1))
        )
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val events = eventRepository.findByExpiredAtAfter(LocalDateTime.now())

        // then
        assertThat(events).hasSize(1)
        assertThat(events[0].id).isEqualTo(activeEvent.id)
    }

    @Test
    @DisplayName("만료되지 않은 이벤트를 최신순으로 페이지네이션 조회한다")
    fun `만료되지 않은 이벤트를 최신순으로 페이지네이션 조회한다`() {
        // given
        for (i in 1..5) {
            eventRepository.save(
                Event(
                    title = "이벤트$i",
                    startedAt = LocalDateTime.now().minusDays(1),
                    expiredAt = LocalDateTime.now().plusDays(i.toLong())
                )
            )
        }
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 3)
        val page = eventRepository.findByExpiredAtAfterOrderByCreatedAtDesc(LocalDateTime.now(), pageable)

        // then
        assertThat(page.content).hasSize(3)
        assertThat(page.totalElements).isEqualTo(5)
        assertThat(page.totalPages).isEqualTo(2)
    }

    @Test
    @DisplayName("만료되지 않은 이벤트를 오래된순으로 페이지네이션 조회한다")
    fun `만료되지 않은 이벤트를 오래된순으로 페이지네이션 조회한다`() {
        // given
        for (i in 1..3) {
            eventRepository.save(
                Event(
                    title = "이벤트$i",
                    startedAt = LocalDateTime.now().minusDays(1),
                    expiredAt = LocalDateTime.now().plusDays(i.toLong())
                )
            )
        }
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10)
        val page = eventRepository.findByExpiredAtAfterOrderByCreatedAtAsc(LocalDateTime.now(), pageable)

        // then
        assertThat(page.content).hasSize(3)
    }

    @Test
    @DisplayName("인기순으로 활성 이벤트를 조회한다")
    fun `인기순으로 활성 이벤트를 조회한다`() {
        // given
        val event1 = eventRepository.save(
            Event(title = "이벤트1", startedAt = LocalDateTime.now().minusDays(1), expiredAt = LocalDateTime.now().plusDays(7), scrapCount = 10)
        )
        val event2 = eventRepository.save(
            Event(title = "이벤트2", startedAt = LocalDateTime.now().minusDays(1), expiredAt = LocalDateTime.now().plusDays(7), scrapCount = 50)
        )
        val event3 = eventRepository.save(
            Event(title = "이벤트3", startedAt = LocalDateTime.now().minusDays(1), expiredAt = LocalDateTime.now().plusDays(7), scrapCount = 30)
        )
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10)
        val page = eventRepository.findActiveEventsByPopular(LocalDateTime.now(), pageable)

        // then
        assertThat(page.content).hasSize(3)
        assertThat(page.content[0].scrapCount).isEqualTo(50)
        assertThat(page.content[1].scrapCount).isEqualTo(30)
        assertThat(page.content[2].scrapCount).isEqualTo(10)
    }

    @Test
    @DisplayName("만료된 이벤트를 페이지네이션으로 조회한다")
    fun `만료된 이벤트를 페이지네이션으로 조회한다`() {
        // given
        eventRepository.save(
            Event(title = "활성 이벤트", startedAt = LocalDateTime.now().minusDays(1), expiredAt = LocalDateTime.now().plusDays(7))
        )
        eventRepository.save(
            Event(title = "만료 이벤트1", startedAt = LocalDateTime.now().minusDays(2), expiredAt = LocalDateTime.now().minusDays(1))
        )
        eventRepository.save(
            Event(title = "만료 이벤트2", startedAt = LocalDateTime.now().minusDays(3), expiredAt = LocalDateTime.now().minusDays(2))
        )
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10)
        val page = eventRepository.findByExpiredAtLessThanEqualOrderByCreatedAtDesc(LocalDateTime.now(), pageable)

        // then
        assertThat(page.content).hasSize(2)
    }

    @Test
    @DisplayName("모든 이벤트를 최신순으로 페이지네이션 조회한다")
    fun `모든 이벤트를 최신순으로 페이지네이션 조회한다`() {
        // given
        for (i in 1..5) {
            eventRepository.save(
                Event(
                    title = "이벤트$i",
                    startedAt = LocalDateTime.now().minusDays(2),
                    expiredAt = if (i <= 2) LocalDateTime.now().minusDays(1) else LocalDateTime.now().plusDays(i.toLong())
                )
            )
        }
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10)
        val page = eventRepository.findAllByOrderByCreatedAtDesc(pageable)

        // then
        assertThat(page.content).hasSize(5)
        assertThat(page.totalElements).isEqualTo(5)
    }
}

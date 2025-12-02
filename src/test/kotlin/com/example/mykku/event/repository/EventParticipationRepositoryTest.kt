package com.example.mykku.event.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventParticipation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("EventParticipationRepository 테스트")
class EventParticipationRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var eventParticipationRepository: EventParticipationRepository

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("회원이 참여한 이벤트 목록을 조회한다")
    fun `회원이 참여한 이벤트 목록을 조회한다`() {
        // given
        val member = createAndSaveMember()
        val event1 = eventRepository.save(
            Event(title = "이벤트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        val event2 = eventRepository.save(
            Event(title = "이벤트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        eventParticipationRepository.save(EventParticipation(member = member, event = event1))
        eventParticipationRepository.save(EventParticipation(member = member, event = event2))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 20)
        val page = eventParticipationRepository.findEventsByMember(member, pageable)

        // then
        assertThat(page.content).hasSize(2)
        assertThat(page.totalElements).isEqualTo(2)
    }

    @Test
    @DisplayName("회원이 참여한 이벤트가 없으면 빈 목록을 반환한다")
    fun `회원이 참여한 이벤트가 없으면 빈 목록을 반환한다`() {
        // given
        val member = createAndSaveMember()
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 20)
        val page = eventParticipationRepository.findEventsByMember(member, pageable)

        // then
        assertThat(page.content).isEmpty()
        assertThat(page.totalElements).isEqualTo(0)
    }

    @Test
    @DisplayName("회원이 참여한 이벤트 목록을 생성일 기준 내림차순으로 조회한다")
    fun `회원이 참여한 이벤트 목록을 생성일 기준 내림차순으로 조회한다`() {
        // given
        val member = createAndSaveMember()
        val event1 = eventRepository.save(
            Event(title = "이벤트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        testEntityManager.flush()
        Thread.sleep(10)
        val event2 = eventRepository.save(
            Event(title = "이벤트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        eventParticipationRepository.save(EventParticipation(member = member, event = event1))
        testEntityManager.flush()
        Thread.sleep(10)
        eventParticipationRepository.save(EventParticipation(member = member, event = event2))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 20)
        val page = eventParticipationRepository.findEventsByMember(member, pageable)

        // then
        assertThat(page.content).hasSize(2)
        assertThat(page.content[0].title).isEqualTo("이벤트2")
        assertThat(page.content[1].title).isEqualTo("이벤트1")
    }
}

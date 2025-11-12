package com.example.mykku.scrap.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.feed.domain.Event
import com.example.mykku.feed.repository.EventRepository
import com.example.mykku.scrap.domain.SaveEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("SaveEventRepository 테스트")
class SaveEventRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var saveEventRepository: SaveEventRepository

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("이벤트를 저장하고 조회한다")
    fun `이벤트를 저장하고 조회한다`() {
        // given
        val member = createAndSaveMember()
        val event = eventRepository.save(
            Event(
                title = "테스트 이벤트",
                isContest = true,
                expiredAt = LocalDateTime.now().plusDays(7)
            )
        )

        val saveEvent = SaveEvent(
            member = member,
            event = event
        )

        // when
        val saved = saveEventRepository.save(saveEvent)
        testEntityManager.flush()
        testEntityManager.clear()

        val found = saveEventRepository.findById(saved.id!!).orElse(null)

        // then
        assertThat(found).isNotNull
        assertThat(found.member.id).isEqualTo(member.id)
        assertThat(found.event.id).isEqualTo(event.id)
    }

    @Test
    @DisplayName("회원과 이벤트로 저장 여부를 확인한다")
    fun `회원과 이벤트로 저장 여부를 확인한다`() {
        // given
        val member = createAndSaveMember()
        val event = eventRepository.save(
            Event(title = "이벤트", isContest = false, expiredAt = LocalDateTime.now().plusDays(1))
        )

        saveEventRepository.save(SaveEvent(member = member, event = event))
        testEntityManager.flush()

        // when
        val exists = saveEventRepository.existsByMemberAndEvent(member, event)

        // then
        assertThat(exists).isTrue()
    }

    @Test
    @DisplayName("회원의 저장된 이벤트를 페이지네이션으로 조회한다")
    fun `회원의 저장된 이벤트를 페이지네이션으로 조회한다`() {
        // given
        val member = createAndSaveMember()
        for (i in 1..5) {
            val event = eventRepository.save(
                Event(
                    title = "이벤트$i",
                    isContest = i % 2 == 0,
                    expiredAt = LocalDateTime.now().plusDays(i.toLong())
                )
            )
            saveEventRepository.save(SaveEvent(member = member, event = event))
        }
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val pageable = PageRequest.of(0, 10)
        val page = saveEventRepository.findByMember(member, pageable)

        // then
        assertThat(page.content).hasSize(5)
        assertThat(page.totalElements).isEqualTo(5)
    }

    @Test
    @DisplayName("회원과 이벤트로 저장을 삭제한다")
    fun `회원과 이벤트로 저장을 삭제한다`() {
        // given
        val member = createAndSaveMember()
        val event = eventRepository.save(
            Event(title = "삭제할 이벤트", isContest = false, expiredAt = LocalDateTime.now().plusDays(1))
        )

        saveEventRepository.save(SaveEvent(member = member, event = event))
        testEntityManager.flush()

        // when
        saveEventRepository.deleteByMemberAndEvent(member, event)
        testEntityManager.flush()

        // then
        val exists = saveEventRepository.existsByMemberAndEvent(member, event)
        assertThat(exists).isFalse()
    }


    @Test
    @DisplayName("회원과 여러 이벤트로 저장된 이벤트 목록을 조회한다")
    fun `회원과 여러 이벤트로 저장된 이벤트 목록을 조회한다`() {
        val member = createAndSaveMember()
        val event1 = eventRepository.save(
            Event(title = "이벤트1", isContest = false, expiredAt = LocalDateTime.now().plusDays(1))
        )
        val event2 = eventRepository.save(
            Event(title = "이벤트2", isContest = false, expiredAt = LocalDateTime.now().plusDays(2))
        )
        val event3 = eventRepository.save(
            Event(title = "이벤트3", isContest = false, expiredAt = LocalDateTime.now().plusDays(3))
        )

        saveEventRepository.save(SaveEvent(member = member, event = event1))
        saveEventRepository.save(SaveEvent(member = member, event = event3))
        testEntityManager.flush()
        testEntityManager.clear()

        val events = listOf(event1, event2, event3)
        val savedEvents = saveEventRepository.findByMemberAndEventIn(member, events)

        assertThat(savedEvents).hasSize(2)
        assertThat(savedEvents.map { it.event.id }).containsExactlyInAnyOrder(event1.id, event3.id)
    }
}

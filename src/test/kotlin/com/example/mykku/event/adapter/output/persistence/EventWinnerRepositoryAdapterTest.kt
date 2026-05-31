package com.example.mykku.event.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.entity.EventParticipationJpaEntity
import com.example.mykku.event.adapter.output.persistence.entity.EventWinnerJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.adapter.output.persistence.repository.EventParticipationJpaRepository
import com.example.mykku.event.adapter.output.persistence.repository.EventWinnerJpaRepository
import com.example.mykku.event.application.port.output.EventWinnerRepository
import com.example.mykku.event.domain.entity.EventWinner
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventParticipationId
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.event.exception.EventException
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("EventWinnerRepositoryAdapter 통합 테스트")
class EventWinnerRepositoryAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var eventWinnerRepository: EventWinnerRepository

    @Autowired
    private lateinit var eventJpaRepository: EventJpaRepository

    @Autowired
    private lateinit var eventParticipationJpaRepository: EventParticipationJpaRepository

    @Autowired
    private lateinit var eventWinnerJpaRepository: EventWinnerJpaRepository

    @Nested
    @DisplayName("save 메서드")
    inner class SaveTest {

        @Test
        @DisplayName("이벤트 당첨자를 저장할 수 있다")
        fun saveEventWinner() {
            val member = createAndSaveMember()
            val event = createAndSaveEvent()
            val participation = createAndSaveParticipation(event, member)

            val winner = EventWinner.create(
                eventId = EventId(event.id!!),
                participationId = EventParticipationId(participation.id!!)
            )

            val saved = eventWinnerRepository.save(winner)

            assertThat(saved.id.value).isGreaterThan(0)
            assertThat(saved.eventId.value).isEqualTo(event.id)
            assertThat(saved.participationId.value).isEqualTo(participation.id)
        }

        @Test
        @DisplayName("존재하지 않는 이벤트에 당첨자 저장시 예외가 발생한다")
        fun saveWinnerWithInvalidEvent() {
            val member = createAndSaveMember()
            val event = createAndSaveEvent()
            val participation = createAndSaveParticipation(event, member)

            val winner = EventWinner.create(
                eventId = EventId(999999L),
                participationId = EventParticipationId(participation.id!!)
            )

            assertThatThrownBy { eventWinnerRepository.save(winner) }
                .isInstanceOf(EventException::class.java)
        }

        @Test
        @DisplayName("존재하지 않는 참여에 당첨자 저장시 예외가 발생한다")
        fun saveWinnerWithInvalidParticipation() {
            val event = createAndSaveEvent()

            val winner = EventWinner.create(
                eventId = EventId(event.id!!),
                participationId = EventParticipationId(999999L)
            )

            assertThatThrownBy { eventWinnerRepository.save(winner) }
                .isInstanceOf(EventException::class.java)
        }
    }

    @Nested
    @DisplayName("saveAll 메서드")
    inner class SaveAllTest {

        @Test
        @DisplayName("여러 당첨자를 한번에 저장할 수 있다")
        fun saveAllWinners() {
            val member1 = createAndSaveMember(memberId = "member1")
            val member2 = createAndSaveMember(memberId = "member2", email = "m2@example.com", socialId = "22222")
            val event = createAndSaveEvent()
            val participation1 = createAndSaveParticipation(event, member1)
            val participation2 = createAndSaveParticipation(event, member2)

            val winners = listOf(
                EventWinner.create(EventId(event.id!!), EventParticipationId(participation1.id!!)),
                EventWinner.create(EventId(event.id!!), EventParticipationId(participation2.id!!))
            )

            val savedWinners = eventWinnerRepository.saveAll(winners)

            assertThat(savedWinners).hasSize(2)
        }

        @Test
        @DisplayName("빈 리스트로 저장하면 빈 리스트를 반환한다")
        fun saveAllEmptyList() {
            val savedWinners = eventWinnerRepository.saveAll(emptyList())

            assertThat(savedWinners).isEmpty()
        }
    }

    @Nested
    @DisplayName("findByEventId 메서드")
    inner class FindByEventIdTest {

        @Test
        @DisplayName("이벤트 ID로 당첨자 목록을 조회할 수 있다")
        fun findByEventId() {
            val member = createAndSaveMember()
            val event = createAndSaveEvent()
            val participation = createAndSaveParticipation(event, member)
            createAndSaveWinner(event, participation)

            val winners = eventWinnerRepository.findByEventId(EventId(event.id!!))

            assertThat(winners).hasSize(1)
        }

        @Test
        @DisplayName("존재하지 않는 이벤트 ID로 조회하면 빈 리스트를 반환한다")
        fun findByEventIdNotFound() {
            val winners = eventWinnerRepository.findByEventId(EventId(999999L))

            assertThat(winners).isEmpty()
        }
    }

    @Nested
    @DisplayName("findByEventIdAndMemberId 메서드")
    inner class FindByEventIdAndMemberIdTest {

        @Test
        @DisplayName("당첨된 회원은 당첨 정보를 조회할 수 있다")
        fun findWinner() {
            val member = createAndSaveMember()
            val event = createAndSaveEvent()
            val participation = createAndSaveParticipation(event, member)
            createAndSaveWinner(event, participation)

            val winner = eventWinnerRepository.findByEventIdAndMemberId(EventId(event.id!!), member.id)

            assertThat(winner).isNotNull
            assertThat(winner!!.eventId.value).isEqualTo(event.id)
        }

        @Test
        @DisplayName("당첨되지 않은 회원은 null을 반환한다")
        fun findNotWinner() {
            val member = createAndSaveMember()
            val event = createAndSaveEvent()
            createAndSaveParticipation(event, member)

            val winner = eventWinnerRepository.findByEventIdAndMemberId(EventId(event.id!!), member.id)

            assertThat(winner).isNull()
        }
    }

    @Nested
    @DisplayName("findByMemberId 메서드")
    inner class FindByMemberIdTest {

        @Test
        @DisplayName("회원이 당첨된 이벤트 목록을 페이징 조회한다")
        fun findByMemberId() {
            val member = createAndSaveMember()
            val event1 = createAndSaveEvent()
            val event2 = createAndSaveEvent()
            createAndSaveWinner(event1, createAndSaveParticipation(event1, member))
            createAndSaveWinner(event2, createAndSaveParticipation(event2, member))

            val page = eventWinnerRepository.findByMemberId(member.id, PageRequest.of(0, 10))

            assertThat(page.content).hasSize(2)
            assertThat(page.totalElements).isEqualTo(2)
        }
    }

    @Nested
    @DisplayName("findByMemberIdAndEventIds 메서드")
    inner class FindByMemberIdAndEventIdsTest {

        @Test
        @DisplayName("회원 ID와 이벤트 ID 목록으로 당첨 목록을 조회한다")
        fun findByMemberIdAndEventIds() {
            val member = createAndSaveMember()
            val event = createAndSaveEvent()
            createAndSaveWinner(event, createAndSaveParticipation(event, member))

            val winners = eventWinnerRepository.findByMemberIdAndEventIds(member.id, listOf(EventId(event.id!!)))

            assertThat(winners).hasSize(1)
        }

        @Test
        @DisplayName("빈 이벤트 ID 목록으로 조회하면 빈 리스트를 반환한다")
        fun findByMemberIdAndEventIdsEmpty() {
            val member = createAndSaveMember()

            val winners = eventWinnerRepository.findByMemberIdAndEventIds(member.id, emptyList())

            assertThat(winners).isEmpty()
        }
    }

    @Nested
    @DisplayName("deleteAllByEventId 메서드")
    inner class DeleteAllByEventIdTest {

        @Test
        @DisplayName("이벤트의 모든 당첨자를 삭제할 수 있다")
        fun deleteAllByEventId() {
            val member = createAndSaveMember()
            val event = createAndSaveEvent()
            createAndSaveWinner(event, createAndSaveParticipation(event, member))

            eventWinnerRepository.deleteAllByEventId(EventId(event.id!!))

            assertThat(eventWinnerRepository.findByEventId(EventId(event.id!!))).isEmpty()
        }

        @Test
        @DisplayName("존재하지 않는 이벤트 ID로 삭제해도 예외가 발생하지 않는다")
        fun deleteAllByEventIdNotFound() {
            eventWinnerRepository.deleteAllByEventId(EventId(999999L))
        }
    }

    private fun createAndSaveEvent(title: String = "테스트 이벤트"): EventJpaEntity {
        val event = EventJpaEntity(
            title = title,
            description = "테스트 이벤트 설명",
            startedAt = LocalDateTime.now().minusDays(7),
            expiredAt = LocalDateTime.now().minusDays(1),
            status = EventStatusType.ACTIVE,
            thumbnailUrl = "https://example.com/thumbnail.jpg"
        )
        return eventJpaRepository.save(event)
    }

    private fun createAndSaveParticipation(
        event: EventJpaEntity,
        member: MemberJpaEntity
    ): EventParticipationJpaEntity {
        return eventParticipationJpaRepository.save(
            EventParticipationJpaEntity(member = member, event = event)
        )
    }

    private fun createAndSaveWinner(
        event: EventJpaEntity,
        participation: EventParticipationJpaEntity
    ): EventWinnerJpaEntity {
        return eventWinnerJpaRepository.save(
            EventWinnerJpaEntity(event = event, participation = participation)
        )
    }
}

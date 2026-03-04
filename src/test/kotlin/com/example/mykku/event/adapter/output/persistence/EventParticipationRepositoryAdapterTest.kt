package com.example.mykku.event.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.application.port.output.EventParticipationRepository
import com.example.mykku.event.domain.entity.EventParticipation
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.event.exception.EventErrorCode
import com.example.mykku.event.exception.EventException
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("EventParticipationRepositoryAdapter 통합 테스트")
class EventParticipationRepositoryAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var eventParticipationRepository: EventParticipationRepository

    @Autowired
    private lateinit var eventJpaRepository: EventJpaRepository

    private lateinit var savedEvent: EventJpaEntity
    private lateinit var savedMember: MemberJpaEntity

    @BeforeEach
    fun setUp() {
        savedEvent = eventJpaRepository.save(createEventJpaEntity())
        savedMember = createAndSaveMember(memberId = "testMember1")
    }

    @Nested
    @DisplayName("save 메서드")
    inner class Save {

        @Test
        @DisplayName("이벤트 참여를 저장하고 ID가 생성된다")
        fun `이벤트 참여 저장 - 정상 케이스`() {
            val participation = createEventParticipation(
                eventId = EventId(savedEvent.id!!),
                memberId = savedMember.id
            )

            val savedParticipation = eventParticipationRepository.save(participation)

            assertThat(savedParticipation.id.value).isGreaterThan(0)
            assertThat(savedParticipation.eventId.value).isEqualTo(savedEvent.id)
        }

        @Test
        @DisplayName("존재하지 않는 이벤트에 참여하면 예외가 발생한다")
        fun `이벤트 참여 저장 - 존재하지 않는 이벤트`() {
            val participation = createEventParticipation(
                eventId = EventId(999999L),
                memberId = savedMember.id
            )

            assertThatThrownBy {
                eventParticipationRepository.save(participation)
            }.isInstanceOf(EventException::class.java)
                .extracting("errorCode")
                .isEqualTo(EventErrorCode.EVENT_NOT_FOUND)
        }

        @Test
        @DisplayName("존재하지 않는 회원이 참여하면 예외가 발생한다")
        fun `이벤트 참여 저장 - 존재하지 않는 회원`() {
            val participation = createEventParticipation(
                eventId = EventId(savedEvent.id!!),
                memberId = 999999L
            )

            assertThatThrownBy {
                eventParticipationRepository.save(participation)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("findByEventId 메서드")
    inner class FindByEventId {

        @Test
        @DisplayName("이벤트 ID로 참여 목록을 페이징 조회한다")
        fun `이벤트 참여 조회 - 정상 케이스`() {
            saveParticipation(savedEvent, savedMember)
            val pageable = PageRequest.of(0, 10)

            val page = eventParticipationRepository.findByEventId(EventId(savedEvent.id!!), pageable)

            assertThat(page.content).hasSize(1)
            assertThat(page.content[0].eventId.value).isEqualTo(savedEvent.id)
        }

        @Test
        @DisplayName("여러 참여자를 페이징으로 조회한다")
        fun `이벤트 참여 조회 - 여러 참여자`() {
            val member2 = createAndSaveMember(memberId = "testMember2", email = "test2@example.com", socialId = "22222")
            val member3 = createAndSaveMember(memberId = "testMember3", email = "test3@example.com", socialId = "33333")
            saveParticipation(savedEvent, savedMember)
            saveParticipation(savedEvent, member2)
            saveParticipation(savedEvent, member3)
            val pageable = PageRequest.of(0, 2)

            val page = eventParticipationRepository.findByEventId(EventId(savedEvent.id!!), pageable)

            assertThat(page.content).hasSize(2)
            assertThat(page.totalElements).isEqualTo(3)
            assertThat(page.totalPages).isEqualTo(2)
        }

        @Test
        @DisplayName("존재하지 않는 이벤트 ID로 조회하면 예외가 발생한다")
        fun `이벤트 참여 조회 - 존재하지 않는 이벤트`() {
            val pageable = PageRequest.of(0, 10)

            assertThatThrownBy {
                eventParticipationRepository.findByEventId(EventId(999999L), pageable)
            }.isInstanceOf(EventException::class.java)
                .extracting("errorCode")
                .isEqualTo(EventErrorCode.EVENT_NOT_FOUND)
        }

        @Test
        @DisplayName("참여자가 없으면 빈 페이지를 반환한다")
        fun `이벤트 참여 조회 - 참여자 없음`() {
            val pageable = PageRequest.of(0, 10)

            val page = eventParticipationRepository.findByEventId(EventId(savedEvent.id!!), pageable)

            assertThat(page.content).isEmpty()
            assertThat(page.totalElements).isEqualTo(0)
        }
    }

    @Nested
    @DisplayName("findEventsByMemberId 메서드")
    inner class FindEventsByMemberId {

        @Test
        @DisplayName("회원이 참여한 이벤트 목록을 조회한다")
        fun `회원 참여 이벤트 조회 - 정상 케이스`() {
            saveParticipation(savedEvent, savedMember)
            val pageable = PageRequest.of(0, 10)

            val page = eventParticipationRepository.findEventsByMemberId(savedMember.id, pageable)

            assertThat(page.content).hasSize(1)
            assertThat(page.content[0].id.value).isEqualTo(savedEvent.id)
        }

        @Test
        @DisplayName("여러 이벤트에 참여한 회원의 이벤트 목록을 조회한다")
        fun `회원 참여 이벤트 조회 - 여러 이벤트`() {
            val event2 = eventJpaRepository.save(createEventJpaEntity())
            val event3 = eventJpaRepository.save(createEventJpaEntity())
            saveParticipation(savedEvent, savedMember)
            saveParticipation(event2, savedMember)
            saveParticipation(event3, savedMember)
            val pageable = PageRequest.of(0, 10)

            val page = eventParticipationRepository.findEventsByMemberId(savedMember.id, pageable)

            assertThat(page.content).hasSize(3)
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 조회하면 예외가 발생한다")
        fun `회원 참여 이벤트 조회 - 존재하지 않는 회원`() {
            val pageable = PageRequest.of(0, 10)

            assertThatThrownBy {
                eventParticipationRepository.findEventsByMemberId(999999L, pageable)
            }.isInstanceOf(MemberException::class.java)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND)
        }

        @Test
        @DisplayName("참여한 이벤트가 없으면 빈 페이지를 반환한다")
        fun `회원 참여 이벤트 조회 - 참여 이벤트 없음`() {
            val pageable = PageRequest.of(0, 10)

            val page = eventParticipationRepository.findEventsByMemberId(savedMember.id, pageable)

            assertThat(page.content).isEmpty()
        }
    }

    @Nested
    @DisplayName("findByMemberIdAndEventIds 메서드")
    inner class FindByMemberIdAndEventIds {

        @Test
        @DisplayName("회원 ID와 이벤트 ID 목록으로 참여 목록을 조회한다")
        fun `회원과 이벤트로 참여 조회 - 정상 케이스`() {
            saveParticipation(savedEvent, savedMember)

            val participations = eventParticipationRepository.findByMemberIdAndEventIds(
                savedMember.id,
                listOf(EventId(savedEvent.id!!))
            )

            assertThat(participations).hasSize(1)
            assertThat(participations[0].eventId.value).isEqualTo(savedEvent.id)
        }

        @Test
        @DisplayName("여러 이벤트 ID로 참여 목록을 조회한다")
        fun `회원과 이벤트로 참여 조회 - 여러 이벤트`() {
            val event2 = eventJpaRepository.save(createEventJpaEntity())
            saveParticipation(savedEvent, savedMember)
            saveParticipation(event2, savedMember)

            val participations = eventParticipationRepository.findByMemberIdAndEventIds(
                savedMember.id,
                listOf(EventId(savedEvent.id!!), EventId(event2.id!!))
            )

            assertThat(participations).hasSize(2)
        }

        @Test
        @DisplayName("빈 이벤트 ID 목록으로 조회하면 빈 리스트를 반환한다")
        fun `회원과 이벤트로 참여 조회 - 빈 이벤트 목록`() {
            val participations = eventParticipationRepository.findByMemberIdAndEventIds(
                savedMember.id,
                emptyList()
            )

            assertThat(participations).isEmpty()
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 조회하면 빈 리스트를 반환한다")
        fun `회원과 이벤트로 참여 조회 - 존재하지 않는 회원`() {
            val participations = eventParticipationRepository.findByMemberIdAndEventIds(
                999999L,
                listOf(EventId(savedEvent.id!!))
            )

            assertThat(participations).isEmpty()
        }

        @Test
        @DisplayName("참여하지 않은 이벤트 ID로 조회하면 빈 리스트를 반환한다")
        fun `회원과 이벤트로 참여 조회 - 참여하지 않은 이벤트`() {
            val participations = eventParticipationRepository.findByMemberIdAndEventIds(
                savedMember.id,
                listOf(EventId(savedEvent.id!!))
            )

            assertThat(participations).isEmpty()
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndEventId 메서드")
    inner class ExistsByMemberIdAndEventId {

        @Test
        @DisplayName("회원이 이벤트에 참여했으면 true를 반환한다")
        fun `참여 여부 확인 - 참여함`() {
            saveParticipation(savedEvent, savedMember)

            val exists = eventParticipationRepository.existsByMemberIdAndEventId(
                savedMember.id,
                EventId(savedEvent.id!!)
            )

            assertThat(exists).isTrue()
        }

        @Test
        @DisplayName("회원이 이벤트에 참여하지 않았으면 false를 반환한다")
        fun `참여 여부 확인 - 참여하지 않음`() {
            val exists = eventParticipationRepository.existsByMemberIdAndEventId(
                savedMember.id,
                EventId(savedEvent.id!!)
            )

            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 확인하면 false를 반환한다")
        fun `참여 여부 확인 - 존재하지 않는 회원`() {
            val exists = eventParticipationRepository.existsByMemberIdAndEventId(
                999999L,
                EventId(savedEvent.id!!)
            )

            assertThat(exists).isFalse()
        }

        @Test
        @DisplayName("존재하지 않는 이벤트 ID로 확인하면 false를 반환한다")
        fun `참여 여부 확인 - 존재하지 않는 이벤트`() {
            val exists = eventParticipationRepository.existsByMemberIdAndEventId(
                savedMember.id,
                EventId(999999L)
            )

            assertThat(exists).isFalse()
        }
    }

    @Nested
    @DisplayName("countByEventId 메서드")
    inner class CountByEventId {

        @Test
        @DisplayName("이벤트의 참여자 수를 반환한다")
        fun `참여자 수 조회 - 정상 케이스`() {
            val member2 = createAndSaveMember(memberId = "testMember2", email = "test2@example.com", socialId = "22222")
            saveParticipation(savedEvent, savedMember)
            saveParticipation(savedEvent, member2)

            val count = eventParticipationRepository.countByEventId(EventId(savedEvent.id!!))

            assertThat(count).isEqualTo(2)
        }

        @Test
        @DisplayName("참여자가 없으면 0을 반환한다")
        fun `참여자 수 조회 - 참여자 없음`() {
            val count = eventParticipationRepository.countByEventId(EventId(savedEvent.id!!))

            assertThat(count).isEqualTo(0)
        }

        @Test
        @DisplayName("존재하지 않는 이벤트 ID로 조회하면 0을 반환한다")
        fun `참여자 수 조회 - 존재하지 않는 이벤트`() {
            val count = eventParticipationRepository.countByEventId(EventId(999999L))

            assertThat(count).isEqualTo(0)
        }
    }

    private fun createEventJpaEntity(): EventJpaEntity {
        return EventJpaEntity(
            title = "테스트 이벤트",
            description = "테스트 이벤트 설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7),
            thumbnailUrl = "https://example.com/thumbnail.jpg",
            scrapCount = 0,
            status = EventStatusType.ACTIVE
        )
    }

    private fun createEventParticipation(eventId: EventId, memberId: Long): EventParticipation {
        return EventParticipation.create(
            eventId = eventId,
            memberId = memberId
        )
    }

    private fun saveParticipation(event: EventJpaEntity, member: MemberJpaEntity) {
        val participation = createEventParticipation(
            eventId = EventId(event.id!!),
            memberId = member.id
        )
        eventParticipationRepository.save(participation)
    }
}

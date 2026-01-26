package com.example.mykku.event.domain

import com.example.mykku.event.domain.entity.EventParticipation
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventParticipationId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("EventParticipation 도메인 엔티티 테스트")
class EventParticipationTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 이벤트 참여를 생성한다")
        fun `이벤트 참여 생성 - 정상 케이스`() {
            val eventId = EventId(1L)
            val memberId = 100L

            val participation = EventParticipation.create(
                eventId = eventId,
                memberId = memberId
            )

            assertThat(participation.eventId).isEqualTo(eventId)
            assertThat(participation.memberId).isEqualTo(memberId)
        }

        @Test
        @DisplayName("이벤트 참여 생성시 id가 0이다")
        fun `이벤트 참여 생성 - id 초기값`() {
            val participation = createEventParticipation()

            assertThat(participation.id.value).isEqualTo(0L)
        }

        @Test
        @DisplayName("이벤트 참여 생성시 createdAt과 updatedAt이 설정된다")
        fun `이벤트 참여 생성 - 시간 설정 검증`() {
            val participation = createEventParticipation()

            assertThat(participation.createdAt).isNotNull()
            assertThat(participation.updatedAt).isNotNull()
        }

        @Test
        @DisplayName("이벤트 참여 생성시 createdAt과 updatedAt이 동일하다")
        fun `이벤트 참여 생성 - 시간 동일 검증`() {
            val participation = createEventParticipation()

            assertThat(participation.createdAt).isEqualTo(participation.updatedAt)
        }

        @Test
        @DisplayName("다른 이벤트에 대한 참여를 생성할 수 있다")
        fun `이벤트 참여 생성 - 다른 이벤트`() {
            val eventId1 = EventId(1L)
            val eventId2 = EventId(2L)
            val memberId = 100L

            val participation1 = EventParticipation.create(
                eventId = eventId1,
                memberId = memberId
            )

            val participation2 = EventParticipation.create(
                eventId = eventId2,
                memberId = memberId
            )

            assertThat(participation1.eventId).isNotEqualTo(participation2.eventId)
            assertThat(participation1.memberId).isEqualTo(participation2.memberId)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 EventParticipation을 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val id = EventParticipationId(1L)
            val eventId = EventId(10L)
            val memberId = 100L

            val participation = EventParticipation.reconstitute(
                id = id,
                eventId = eventId,
                memberId = memberId,
                createdAt = now,
                updatedAt = now
            )

            assertThat(participation.id).isEqualTo(id)
            assertThat(participation.eventId).isEqualTo(eventId)
            assertThat(participation.memberId).isEqualTo(memberId)
            assertThat(participation.createdAt).isEqualTo(now)
            assertThat(participation.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("다른 시간으로 복원할 수 있다")
        fun `복원 - 다른 시간`() {
            val createdAt = LocalDateTime.now().minusDays(1)
            val updatedAt = LocalDateTime.now()

            val participation = EventParticipation.reconstitute(
                id = EventParticipationId(1L),
                eventId = EventId(1L),
                memberId = 100L,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(participation.createdAt).isEqualTo(createdAt)
            assertThat(participation.updatedAt).isEqualTo(updatedAt)
            assertThat(participation.createdAt).isNotEqualTo(participation.updatedAt)
        }

        @Test
        @DisplayName("다양한 회원의 참여를 복원할 수 있다")
        fun `복원 - 다양한 회원`() {
            val now = LocalDateTime.now()
            val eventId = EventId(1L)

            val participation1 = EventParticipation.reconstitute(
                id = EventParticipationId(1L),
                eventId = eventId,
                memberId = 100L,
                createdAt = now,
                updatedAt = now
            )

            val participation2 = EventParticipation.reconstitute(
                id = EventParticipationId(2L),
                eventId = eventId,
                memberId = 200L,
                createdAt = now,
                updatedAt = now
            )

            assertThat(participation1.memberId).isEqualTo(100L)
            assertThat(participation2.memberId).isEqualTo(200L)
            assertThat(participation1.eventId).isEqualTo(participation2.eventId)
        }

        @Test
        @DisplayName("특정 id로 복원할 수 있다")
        fun `복원 - 특정 id`() {
            val now = LocalDateTime.now()
            val specificId = EventParticipationId(999L)

            val participation = EventParticipation.reconstitute(
                id = specificId,
                eventId = EventId(1L),
                memberId = 100L,
                createdAt = now,
                updatedAt = now
            )

            assertThat(participation.id.value).isEqualTo(999L)
        }
    }

    private fun createEventParticipation(): EventParticipation {
        return EventParticipation.create(
            eventId = EventId(1L),
            memberId = 100L
        )
    }
}

package com.example.mykku.event.domain

import com.example.mykku.event.domain.entity.EventWinner
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventParticipationId
import com.example.mykku.event.domain.vo.EventWinnerId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("EventWinner 도메인 엔티티 테스트")
class EventWinnerTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 이벤트 당첨자를 생성한다")
        fun `이벤트 당첨자 생성 - 정상 케이스`() {
            val eventId = EventId(10L)
            val participationId = EventParticipationId(100L)

            val winner = EventWinner.create(eventId = eventId, participationId = participationId)

            assertThat(winner.eventId).isEqualTo(eventId)
            assertThat(winner.participationId).isEqualTo(participationId)
        }

        @Test
        @DisplayName("이벤트 당첨자 생성시 id가 0이다")
        fun `이벤트 당첨자 생성 - id 초기값`() {
            val winner = createWinner()

            assertThat(winner.id.value).isEqualTo(0L)
        }

        @Test
        @DisplayName("이벤트 당첨자 생성시 createdAt과 updatedAt이 동일하다")
        fun `이벤트 당첨자 생성 - 시간 동일 검증`() {
            val winner = createWinner()

            assertThat(winner.createdAt).isEqualTo(winner.updatedAt)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 EventWinner를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val id = EventWinnerId(1L)
            val eventId = EventId(10L)
            val participationId = EventParticipationId(100L)

            val winner = EventWinner.reconstitute(
                id = id,
                eventId = eventId,
                participationId = participationId,
                createdAt = now,
                updatedAt = now
            )

            assertThat(winner.id).isEqualTo(id)
            assertThat(winner.eventId).isEqualTo(eventId)
            assertThat(winner.participationId).isEqualTo(participationId)
            assertThat(winner.createdAt).isEqualTo(now)
            assertThat(winner.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("다른 시간으로 복원할 수 있다")
        fun `복원 - 다른 시간`() {
            val createdAt = LocalDateTime.of(2024, 1, 1, 10, 0)
            val updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30)

            val winner = EventWinner.reconstitute(
                id = EventWinnerId(1L),
                eventId = EventId(10L),
                participationId = EventParticipationId(100L),
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(winner.createdAt).isEqualTo(createdAt)
            assertThat(winner.updatedAt).isEqualTo(updatedAt)
            assertThat(winner.createdAt).isNotEqualTo(winner.updatedAt)
        }
    }

    private fun createWinner(): EventWinner {
        return EventWinner.create(
            eventId = EventId(10L),
            participationId = EventParticipationId(100L)
        )
    }
}

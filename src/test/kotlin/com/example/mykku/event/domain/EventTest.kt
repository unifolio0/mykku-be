package com.example.mykku.event.domain

import com.example.mykku.event.domain.entity.Event
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventStatusType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("Event 도메인 엔티티 테스트")
class EventTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 이벤트를 생성한다")
        fun `이벤트 생성 - 정상 케이스`() {
            val startedAt = LocalDateTime.now().plusDays(1)
            val expiredAt = LocalDateTime.now().plusDays(7)

            val event = Event.create(
                title = "테스트 이벤트",
                description = "이벤트 설명",
                startedAt = startedAt,
                expiredAt = expiredAt
            )

            assertThat(event.id.value).isEqualTo(0L)
            assertThat(event.title).isEqualTo("테스트 이벤트")
            assertThat(event.description).isEqualTo("이벤트 설명")
            assertThat(event.startedAt).isEqualTo(startedAt)
            assertThat(event.expiredAt).isEqualTo(expiredAt)
        }

        @Test
        @DisplayName("이벤트 생성시 status가 ACTIVE이다")
        fun `이벤트 생성 - 기본 상태 검증`() {
            val event = createEvent()

            assertThat(event.status).isEqualTo(EventStatusType.ACTIVE)
        }

        @Test
        @DisplayName("이벤트 생성시 scrapCount가 0이다")
        fun `이벤트 생성 - scrapCount 초기값`() {
            val event = createEvent()

            assertThat(event.scrapCount).isEqualTo(0)
        }

        @Test
        @DisplayName("이벤트 생성시 createdAt과 updatedAt이 설정된다")
        fun `이벤트 생성 - 시간 설정 검증`() {
            val event = createEvent()

            assertThat(event.createdAt).isNotNull()
            assertThat(event.updatedAt).isNotNull()
        }

        @Test
        @DisplayName("설명 없이 이벤트를 생성할 수 있다")
        fun `이벤트 생성 - 설명 없음`() {
            val event = Event.create(
                title = "테스트 이벤트",
                description = null,
                startedAt = LocalDateTime.now().plusDays(1),
                expiredAt = LocalDateTime.now().plusDays(7)
            )

            assertThat(event.description).isNull()
        }
    }

    @Nested
    @DisplayName("updateStatus 메서드")
    inner class UpdateStatus {

        @Test
        @DisplayName("이벤트 상태를 EXPIRED로 변경할 수 있다")
        fun `상태 변경 - EXPIRED`() {
            val event = createEvent()

            event.updateStatus(EventStatusType.EXPIRED)

            assertThat(event.status).isEqualTo(EventStatusType.EXPIRED)
        }

        @Test
        @DisplayName("이벤트 상태를 WINNER_SELECTING으로 변경할 수 있다")
        fun `상태 변경 - WINNER_SELECTING`() {
            val event = createEvent()

            event.updateStatus(EventStatusType.WINNER_SELECTING)

            assertThat(event.status).isEqualTo(EventStatusType.WINNER_SELECTING)
        }

        @Test
        @DisplayName("이벤트 상태를 WINNER_SELECTED로 변경할 수 있다")
        fun `상태 변경 - WINNER_SELECTED`() {
            val event = createEvent()

            event.updateStatus(EventStatusType.WINNER_SELECTED)

            assertThat(event.status).isEqualTo(EventStatusType.WINNER_SELECTED)
        }

        @Test
        @DisplayName("EXPIRED 상태에서 ACTIVE로 다시 변경할 수 있다")
        fun `상태 변경 - ACTIVE 복원`() {
            val event = createEvent()
            event.updateStatus(EventStatusType.EXPIRED)

            event.updateStatus(EventStatusType.ACTIVE)

            assertThat(event.status).isEqualTo(EventStatusType.ACTIVE)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 Event를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val startedAt = now.plusDays(1)
            val expiredAt = now.plusDays(7)

            val event = Event.reconstitute(
                id = EventId(1L),
                title = "복원된 이벤트",
                description = "설명",
                startedAt = startedAt,
                expiredAt = expiredAt,
                scrapCount = 10,
                status = EventStatusType.EXPIRED,
                createdAt = now,
                updatedAt = now
            )

            assertThat(event.id.value).isEqualTo(1L)
            assertThat(event.title).isEqualTo("복원된 이벤트")
            assertThat(event.scrapCount).isEqualTo(10)
            assertThat(event.status).isEqualTo(EventStatusType.EXPIRED)
        }

        @Test
        @DisplayName("다양한 상태로 복원할 수 있다")
        fun `복원 - 다양한 상태`() {
            val now = LocalDateTime.now()

            val activeEvent = Event.reconstitute(
                id = EventId(1L),
                title = "활성 이벤트",
                description = null,
                startedAt = now,
                expiredAt = now.plusDays(7),
                scrapCount = 0,
                status = EventStatusType.ACTIVE,
                createdAt = now,
                updatedAt = now
            )

            val winnerSelectedEvent = Event.reconstitute(
                id = EventId(2L),
                title = "수상자 선정 완료 이벤트",
                description = null,
                startedAt = now,
                expiredAt = now.plusDays(7),
                scrapCount = 0,
                status = EventStatusType.WINNER_SELECTED,
                createdAt = now,
                updatedAt = now
            )

            assertThat(activeEvent.status).isEqualTo(EventStatusType.ACTIVE)
            assertThat(winnerSelectedEvent.status).isEqualTo(EventStatusType.WINNER_SELECTED)
        }

        @Test
        @DisplayName("설명이 null인 Event를 복원할 수 있다")
        fun `복원 - 설명 없음`() {
            val now = LocalDateTime.now()

            val event = Event.reconstitute(
                id = EventId(1L),
                title = "설명 없는 이벤트",
                description = null,
                startedAt = now,
                expiredAt = now.plusDays(7),
                scrapCount = 5,
                status = EventStatusType.ACTIVE,
                createdAt = now,
                updatedAt = now
            )

            assertThat(event.description).isNull()
        }
    }

    @Nested
    @DisplayName("상수 검증")
    inner class Constants {

        @Test
        @DisplayName("IMAGE_MAX_COUNT가 10이다")
        fun `상수 검증 - IMAGE_MAX_COUNT`() {
            assertThat(Event.IMAGE_MAX_COUNT).isEqualTo(10)
        }
    }

    private fun createEvent(): Event {
        return Event.create(
            title = "테스트 이벤트",
            description = "이벤트 설명",
            startedAt = LocalDateTime.now().plusDays(1),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
    }
}

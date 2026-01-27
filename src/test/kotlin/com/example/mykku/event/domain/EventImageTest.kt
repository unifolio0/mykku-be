package com.example.mykku.event.domain

import com.example.mykku.event.domain.entity.EventImage
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventImageId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("EventImage 도메인 엔티티 테스트")
class EventImageTest {

    @Nested
    @DisplayName("create 메서드")
    inner class Create {

        @Test
        @DisplayName("정상적으로 이벤트 이미지를 생성한다")
        fun `이벤트 이미지 생성 - 정상 케이스`() {
            val url = "https://example.com/image.jpg"
            val orderIndex = 1
            val eventId = EventId(1L)

            val eventImage = EventImage.create(
                url = url,
                orderIndex = orderIndex,
                eventId = eventId
            )

            assertThat(eventImage.url).isEqualTo(url)
            assertThat(eventImage.orderIndex).isEqualTo(orderIndex)
            assertThat(eventImage.eventId).isEqualTo(eventId)
        }

        @Test
        @DisplayName("이벤트 이미지 생성시 id가 0이다")
        fun `이벤트 이미지 생성 - id 초기값`() {
            val eventImage = createEventImage()

            assertThat(eventImage.id.value).isEqualTo(0L)
        }

        @Test
        @DisplayName("이벤트 이미지 생성시 createdAt과 updatedAt이 설정된다")
        fun `이벤트 이미지 생성 - 시간 설정 검증`() {
            val eventImage = createEventImage()

            assertThat(eventImage.createdAt).isNotNull()
            assertThat(eventImage.updatedAt).isNotNull()
        }

        @Test
        @DisplayName("이벤트 이미지 생성시 createdAt과 updatedAt이 동일하다")
        fun `이벤트 이미지 생성 - 시간 동일 검증`() {
            val eventImage = createEventImage()

            assertThat(eventImage.createdAt).isEqualTo(eventImage.updatedAt)
        }

        @Test
        @DisplayName("orderIndex가 0인 이벤트 이미지를 생성할 수 있다")
        fun `이벤트 이미지 생성 - orderIndex 0`() {
            val eventImage = EventImage.create(
                url = "https://example.com/image.jpg",
                orderIndex = 0,
                eventId = EventId(1L)
            )

            assertThat(eventImage.orderIndex).isEqualTo(0)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 EventImage를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val id = EventImageId(1L)
            val url = "https://example.com/restored-image.jpg"
            val orderIndex = 2
            val eventId = EventId(10L)

            val eventImage = EventImage.reconstitute(
                id = id,
                url = url,
                orderIndex = orderIndex,
                eventId = eventId,
                createdAt = now,
                updatedAt = now
            )

            assertThat(eventImage.id).isEqualTo(id)
            assertThat(eventImage.url).isEqualTo(url)
            assertThat(eventImage.orderIndex).isEqualTo(orderIndex)
            assertThat(eventImage.eventId).isEqualTo(eventId)
            assertThat(eventImage.createdAt).isEqualTo(now)
            assertThat(eventImage.updatedAt).isEqualTo(now)
        }

        @Test
        @DisplayName("다른 시간으로 복원할 수 있다")
        fun `복원 - 다른 시간`() {
            val createdAt = LocalDateTime.now().minusDays(1)
            val updatedAt = LocalDateTime.now()

            val eventImage = EventImage.reconstitute(
                id = EventImageId(1L),
                url = "https://example.com/image.jpg",
                orderIndex = 1,
                eventId = EventId(1L),
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            assertThat(eventImage.createdAt).isEqualTo(createdAt)
            assertThat(eventImage.updatedAt).isEqualTo(updatedAt)
            assertThat(eventImage.createdAt).isNotEqualTo(eventImage.updatedAt)
        }

        @Test
        @DisplayName("다양한 orderIndex로 복원할 수 있다")
        fun `복원 - 다양한 orderIndex`() {
            val now = LocalDateTime.now()

            val firstImage = EventImage.reconstitute(
                id = EventImageId(1L),
                url = "https://example.com/first.jpg",
                orderIndex = 0,
                eventId = EventId(1L),
                createdAt = now,
                updatedAt = now
            )

            val lastImage = EventImage.reconstitute(
                id = EventImageId(2L),
                url = "https://example.com/last.jpg",
                orderIndex = 9,
                eventId = EventId(1L),
                createdAt = now,
                updatedAt = now
            )

            assertThat(firstImage.orderIndex).isEqualTo(0)
            assertThat(lastImage.orderIndex).isEqualTo(9)
        }
    }

    private fun createEventImage(): EventImage {
        return EventImage.create(
            url = "https://example.com/test-image.jpg",
            orderIndex = 1,
            eventId = EventId(1L)
        )
    }
}

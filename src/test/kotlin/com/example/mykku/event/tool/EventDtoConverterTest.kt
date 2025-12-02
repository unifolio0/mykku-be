package com.example.mykku.event.tool

import com.example.mykku.BaseToolTest
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("EventDtoConverter 테스트")
class EventDtoConverterTest : BaseToolTest() {

    private val eventDtoConverter = EventDtoConverter()

    @Test
    @DisplayName("Event를 EventListResponse로 변환한다")
    fun `Event를 EventListResponse로 변환한다`() {
        // given
        val event = Event(
            id = 1L,
            title = "테스트 이벤트",
            description = "설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val images = listOf(
            EventImage(id = 1L, url = "url1", orderIndex = 0, event = event),
            EventImage(id = 2L, url = "url2", orderIndex = 1, event = event)
        )

        // when
        val result = eventDtoConverter.toEventListResponse(event, images, true)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 이벤트")
        assertThat(result.thumbnailUrl).isEqualTo("url1")
        assertThat(result.isSaved).isTrue()
    }

    @Test
    @DisplayName("이미지가 없을 때 thumbnailUrl은 null이다")
    fun `이미지가 없을 때 thumbnailUrl은 null이다`() {
        // given
        val event = Event(
            id = 1L,
            title = "테스트 이벤트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )

        // when
        val result = eventDtoConverter.toEventListResponse(event, emptyList(), false)

        // then
        assertThat(result.thumbnailUrl).isNull()
        assertThat(result.isSaved).isFalse()
    }

    @Test
    @DisplayName("이미지가 orderIndex 순으로 정렬되어 첫 번째가 thumbnail이 된다")
    fun `이미지가 orderIndex 순으로 정렬되어 첫 번째가 thumbnail이 된다`() {
        // given
        val event = Event(
            id = 1L,
            title = "테스트 이벤트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        val images = listOf(
            EventImage(id = 2L, url = "url2", orderIndex = 2, event = event),
            EventImage(id = 1L, url = "url0", orderIndex = 0, event = event),
            EventImage(id = 3L, url = "url1", orderIndex = 1, event = event)
        )

        // when
        val result = eventDtoConverter.toEventListResponse(event, images, false)

        // then
        assertThat(result.thumbnailUrl).isEqualTo("url0")
    }

    @Test
    @DisplayName("Event를 EventDetailResponse로 변환한다")
    fun `Event를 EventDetailResponse로 변환한다`() {
        // given
        val event = Event(
            id = 1L,
            title = "테스트 이벤트",
            description = "이벤트 설명",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        initializeBaseEntityFields(event, LocalDateTime.now())

        val images = listOf(
            EventImage(id = 1L, url = "url1", orderIndex = 0, event = event),
            EventImage(id = 2L, url = "url2", orderIndex = 1, event = event)
        )

        // when
        val result = eventDtoConverter.toEventDetailResponse(event, images, true)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("테스트 이벤트")
        assertThat(result.description).isEqualTo("이벤트 설명")
        assertThat(result.images).hasSize(2)
        assertThat(result.images[0].url).isEqualTo("url1")
        assertThat(result.images[0].orderIndex).isEqualTo(0)
        assertThat(result.isSaved).isTrue()
    }

    @Test
    @DisplayName("EventDetailResponse 이미지가 orderIndex 순으로 정렬된다")
    fun `EventDetailResponse 이미지가 orderIndex 순으로 정렬된다`() {
        // given
        val event = Event(
            id = 1L,
            title = "테스트 이벤트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        initializeBaseEntityFields(event, LocalDateTime.now())

        val images = listOf(
            EventImage(id = 3L, url = "url2", orderIndex = 2, event = event),
            EventImage(id = 1L, url = "url0", orderIndex = 0, event = event),
            EventImage(id = 2L, url = "url1", orderIndex = 1, event = event)
        )

        // when
        val result = eventDtoConverter.toEventDetailResponse(event, images, false)

        // then
        assertThat(result.images).hasSize(3)
        assertThat(result.images[0].orderIndex).isEqualTo(0)
        assertThat(result.images[1].orderIndex).isEqualTo(1)
        assertThat(result.images[2].orderIndex).isEqualTo(2)
    }

    @Test
    @DisplayName("이미지 없이 EventDetailResponse를 생성한다")
    fun `이미지 없이 EventDetailResponse를 생성한다`() {
        // given
        val event = Event(
            id = 1L,
            title = "테스트 이벤트",
            startedAt = LocalDateTime.now(),
            expiredAt = LocalDateTime.now().plusDays(7)
        )
        initializeBaseEntityFields(event, LocalDateTime.now())

        // when
        val result = eventDtoConverter.toEventDetailResponse(event, emptyList(), false)

        // then
        assertThat(result.images).isEmpty()
        assertThat(result.isSaved).isFalse()
    }
}

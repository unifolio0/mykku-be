package com.example.mykku.event.adapter.output.persistence

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.event.adapter.output.persistence.entity.EventJpaEntity
import com.example.mykku.event.adapter.output.persistence.repository.EventJpaRepository
import com.example.mykku.event.application.port.output.EventImageRepository
import com.example.mykku.event.domain.entity.EventImage
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.event.exception.EventErrorCode
import com.example.mykku.event.exception.EventException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DisplayName("EventImageRepositoryAdapter 통합 테스트")
class EventImageRepositoryAdapterTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var eventImageRepository: EventImageRepository

    @Autowired
    private lateinit var eventJpaRepository: EventJpaRepository

    private lateinit var savedEvent: EventJpaEntity

    @BeforeEach
    fun setUp() {
        savedEvent = eventJpaRepository.save(createEventJpaEntity())
    }

    @Nested
    @DisplayName("save 메서드")
    inner class Save {

        @Test
        @DisplayName("이벤트 이미지를 저장하고 ID가 생성된다")
        fun `이벤트 이미지 저장 - 정상 케이스`() {
            val eventImage = createEventImage(EventId(savedEvent.id!!))

            val savedImage = eventImageRepository.save(eventImage)

            assertThat(savedImage.id.value).isGreaterThan(0)
            assertThat(savedImage.url).isEqualTo(eventImage.url)
            assertThat(savedImage.orderIndex).isEqualTo(eventImage.orderIndex)
            assertThat(savedImage.eventId.value).isEqualTo(savedEvent.id)
        }

        @Test
        @DisplayName("존재하지 않는 이벤트에 이미지를 저장하면 예외가 발생한다")
        fun `이벤트 이미지 저장 - 존재하지 않는 이벤트`() {
            val nonExistentEventId = EventId(999999L)
            val eventImage = createEventImage(nonExistentEventId)

            assertThatThrownBy {
                eventImageRepository.save(eventImage)
            }.isInstanceOf(EventException::class.java)
                .extracting("errorCode")
                .isEqualTo(EventErrorCode.EVENT_NOT_FOUND)
        }

        @Test
        @DisplayName("orderIndex가 0인 이미지를 저장할 수 있다")
        fun `이벤트 이미지 저장 - orderIndex 0`() {
            val eventImage = EventImage.create(
                url = "https://example.com/image.jpg",
                orderIndex = 0,
                eventId = EventId(savedEvent.id!!)
            )

            val savedImage = eventImageRepository.save(eventImage)

            assertThat(savedImage.orderIndex).isEqualTo(0)
        }
    }

    @Nested
    @DisplayName("saveAll 메서드")
    inner class SaveAll {

        @Test
        @DisplayName("여러 이벤트 이미지를 한 번에 저장한다")
        fun `여러 이미지 저장 - 정상 케이스`() {
            val eventImages = listOf(
                createEventImage(EventId(savedEvent.id!!), 0),
                createEventImage(EventId(savedEvent.id!!), 1),
                createEventImage(EventId(savedEvent.id!!), 2)
            )

            val savedImages = eventImageRepository.saveAll(eventImages)

            assertThat(savedImages).hasSize(3)
            assertThat(savedImages).allMatch { it.id.value > 0 }
            assertThat(savedImages.map { it.orderIndex }).containsExactly(0, 1, 2)
        }

        @Test
        @DisplayName("빈 리스트를 저장하면 빈 리스트를 반환한다")
        fun `여러 이미지 저장 - 빈 리스트`() {
            val savedImages = eventImageRepository.saveAll(emptyList())

            assertThat(savedImages).isEmpty()
        }

        @Test
        @DisplayName("존재하지 않는 이벤트에 이미지를 저장하면 예외가 발생한다")
        fun `여러 이미지 저장 - 존재하지 않는 이벤트`() {
            val nonExistentEventId = EventId(999999L)
            val eventImages = listOf(
                createEventImage(nonExistentEventId, 0)
            )

            assertThatThrownBy {
                eventImageRepository.saveAll(eventImages)
            }.isInstanceOf(EventException::class.java)
                .extracting("errorCode")
                .isEqualTo(EventErrorCode.EVENT_NOT_FOUND)
        }

        @Test
        @DisplayName("여러 이벤트의 이미지를 한 번에 저장할 수 있다")
        fun `여러 이미지 저장 - 다른 이벤트`() {
            val anotherEvent = eventJpaRepository.save(createEventJpaEntity())
            val eventImages = listOf(
                createEventImage(EventId(savedEvent.id!!), 0),
                createEventImage(EventId(anotherEvent.id!!), 0)
            )

            val savedImages = eventImageRepository.saveAll(eventImages)

            assertThat(savedImages).hasSize(2)
            assertThat(savedImages.map { it.eventId.value }.distinct()).hasSize(2)
        }
    }

    @Nested
    @DisplayName("findByEventIds 메서드")
    inner class FindByEventIds {

        @Test
        @DisplayName("이벤트 ID 목록으로 이미지들을 조회한다")
        fun `이벤트 ID로 이미지 조회 - 정상 케이스`() {
            val eventImages = listOf(
                createEventImage(EventId(savedEvent.id!!), 0),
                createEventImage(EventId(savedEvent.id!!), 1)
            )
            eventImageRepository.saveAll(eventImages)

            val foundImages = eventImageRepository.findByEventIds(listOf(EventId(savedEvent.id!!)))

            assertThat(foundImages).hasSize(2)
            assertThat(foundImages).allMatch { it.eventId.value == savedEvent.id }
        }

        @Test
        @DisplayName("여러 이벤트 ID로 이미지들을 조회한다")
        fun `이벤트 ID로 이미지 조회 - 여러 이벤트`() {
            val anotherEvent = eventJpaRepository.save(createEventJpaEntity())
            eventImageRepository.saveAll(
                listOf(
                    createEventImage(EventId(savedEvent.id!!), 0),
                    createEventImage(EventId(anotherEvent.id!!), 0)
                )
            )

            val foundImages = eventImageRepository.findByEventIds(
                listOf(EventId(savedEvent.id!!), EventId(anotherEvent.id!!))
            )

            assertThat(foundImages).hasSize(2)
        }

        @Test
        @DisplayName("빈 이벤트 ID 목록으로 조회하면 빈 리스트를 반환한다")
        fun `이벤트 ID로 이미지 조회 - 빈 리스트`() {
            val foundImages = eventImageRepository.findByEventIds(emptyList())

            assertThat(foundImages).isEmpty()
        }

        @Test
        @DisplayName("존재하지 않는 이벤트 ID로 조회하면 빈 리스트를 반환한다")
        fun `이벤트 ID로 이미지 조회 - 존재하지 않는 이벤트`() {
            val nonExistentEventId = EventId(999999L)

            val foundImages = eventImageRepository.findByEventIds(listOf(nonExistentEventId))

            assertThat(foundImages).isEmpty()
        }

        @Test
        @DisplayName("이미지가 없는 이벤트 ID로 조회하면 빈 리스트를 반환한다")
        fun `이벤트 ID로 이미지 조회 - 이미지 없음`() {
            val foundImages = eventImageRepository.findByEventIds(listOf(EventId(savedEvent.id!!)))

            assertThat(foundImages).isEmpty()
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

    private fun createEventImage(eventId: EventId, orderIndex: Int = 0): EventImage {
        return EventImage.create(
            url = "https://example.com/image_$orderIndex.jpg",
            orderIndex = orderIndex,
            eventId = eventId
        )
    }
}

package com.example.mykku.event.repository

import com.example.mykku.BaseRepositoryTest
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventImage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDateTime

@DisplayName("EventImageRepository 테스트")
class EventImageRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var eventImageRepository: EventImageRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    @DisplayName("이벤트 이미지를 저장하고 조회한다")
    fun `이벤트 이미지를 저장하고 조회한다`() {
        // given
        val event = eventRepository.save(
            Event(title = "테스트 이벤트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )

        val eventImage = EventImage(
            url = "https://example.com/image.jpg",
            orderIndex = 0,
            event = event
        )

        // when
        val savedImage = eventImageRepository.save(eventImage)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundImage = eventImageRepository.findById(savedImage.id!!).orElse(null)

        // then
        assertThat(foundImage).isNotNull
        assertThat(foundImage.url).isEqualTo("https://example.com/image.jpg")
        assertThat(foundImage.orderIndex).isEqualTo(0)
    }

    @Test
    @DisplayName("여러 이벤트의 이미지를 조회한다")
    fun `여러 이벤트의 이미지를 조회한다`() {
        // given
        val event1 = eventRepository.save(
            Event(title = "이벤트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        val event2 = eventRepository.save(
            Event(title = "이벤트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )
        val event3 = eventRepository.save(
            Event(title = "이벤트3", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )

        eventImageRepository.save(EventImage(url = "url1", orderIndex = 0, event = event1))
        eventImageRepository.save(EventImage(url = "url2", orderIndex = 1, event = event1))
        eventImageRepository.save(EventImage(url = "url3", orderIndex = 0, event = event2))
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val images = eventImageRepository.findByEventIn(listOf(event1, event2))

        // then
        assertThat(images).hasSize(3)
    }

    @Test
    @DisplayName("이벤트 목록이 비어있을 때 빈 리스트를 반환한다")
    fun `이벤트 목록이 비어있을 때 빈 리스트를 반환한다`() {
        // when
        val images = eventImageRepository.findByEventIn(emptyList())

        // then
        assertThat(images).isEmpty()
    }

    @Test
    @DisplayName("이벤트에 여러 이미지가 있을 때 모든 이미지를 조회한다")
    fun `이벤트에 여러 이미지가 있을 때 모든 이미지를 조회한다`() {
        // given
        val event = eventRepository.save(
            Event(title = "이벤트", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        )

        for (i in 0 until 5) {
            eventImageRepository.save(
                EventImage(url = "url$i", orderIndex = i, event = event)
            )
        }
        testEntityManager.flush()
        testEntityManager.clear()

        // when
        val images = eventImageRepository.findByEventIn(listOf(event))

        // then
        assertThat(images).hasSize(5)
        assertThat(images.map { it.orderIndex }).containsExactlyInAnyOrder(0, 1, 2, 3, 4)
    }
}

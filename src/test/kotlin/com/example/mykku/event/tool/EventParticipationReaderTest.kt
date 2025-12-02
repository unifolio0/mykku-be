package com.example.mykku.event.tool

import com.example.mykku.BaseServiceTest
import com.example.mykku.event.domain.Event
import com.example.mykku.event.repository.EventParticipationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@DisplayName("EventParticipationReader 테스트")
class EventParticipationReaderTest : BaseServiceTest() {

    @Mock
    private lateinit var eventParticipationRepository: EventParticipationRepository

    @InjectMocks
    private lateinit var eventParticipationReader: EventParticipationReader

    @Test
    @DisplayName("회원이 참여한 이벤트 목록을 조회한다")
    fun `회원이 참여한 이벤트 목록을 조회한다`() {
        // given
        val member = createTestMember()
        val event1 = Event(id = 1L, title = "이벤트1", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val event2 = Event(id = 2L, title = "이벤트2", startedAt = LocalDateTime.now(), expiredAt = LocalDateTime.now().plusDays(7))
        val events = listOf(event1, event2)
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(events, pageable, events.size.toLong())

        whenever(eventParticipationRepository.findEventsByMember(eq(member), any())).thenReturn(page)

        // when
        val result = eventParticipationReader.getParticipatedEvents(member, pageable)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2)
        assertThat(result.content[0].title).isEqualTo("이벤트1")
        assertThat(result.content[1].title).isEqualTo("이벤트2")
    }

    @Test
    @DisplayName("참여한 이벤트가 없으면 빈 목록을 반환한다")
    fun `참여한 이벤트가 없으면 빈 목록을 반환한다`() {
        // given
        val member = createTestMember()
        val pageable = PageRequest.of(0, 20)
        val emptyPage = PageImpl<Event>(emptyList(), pageable, 0)

        whenever(eventParticipationRepository.findEventsByMember(eq(member), any())).thenReturn(emptyPage)

        // when
        val result = eventParticipationReader.getParticipatedEvents(member, pageable)

        // then
        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0)
    }
}

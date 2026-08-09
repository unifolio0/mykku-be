package com.example.mykku.fannote.application.usecase

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.fannote.application.port.output.FanNotePageRepository
import com.example.mykku.fannote.application.port.output.FanNoteRepository
import com.example.mykku.fannote.domain.entity.FanNote
import com.example.mykku.fannote.domain.vo.FanNoteId
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("덕질노트 열람 활동 이벤트 발행 단위 테스트")
class GetFanNoteDetailActivityPublishTest {

    @Mock
    private lateinit var fanNoteRepository: FanNoteRepository

    @Mock
    private lateinit var fanNotePageRepository: FanNotePageRepository

    @Mock
    private lateinit var activityEventPublisher: ActivityEventPublisher

    @Test
    @DisplayName("로그인한 회원이 열람하면 FANNOTE_VIEW 이벤트를 발행한다")
    fun publishesForLoggedInMember() {
        val useCase = GetFanNoteDetailUseCaseImpl(fanNoteRepository, fanNotePageRepository, activityEventPublisher)
        whenever(fanNoteRepository.findById(FanNoteId.of(7L))).thenReturn(stubFanNote())
        whenever(fanNotePageRepository.findByFanNoteIdOrderByPageNumber(FanNoteId.of(7L))).thenReturn(emptyList())

        useCase.execute(7L, 1L)

        verify(activityEventPublisher).publish(ActivityEvent(1L, ActivityType.FANNOTE_VIEW))
    }

    @Test
    @DisplayName("비로그인 열람은 이벤트를 발행하지 않는다")
    fun doesNotPublishForAnonymous() {
        val useCase = GetFanNoteDetailUseCaseImpl(fanNoteRepository, fanNotePageRepository, activityEventPublisher)
        whenever(fanNoteRepository.findById(FanNoteId.of(7L))).thenReturn(stubFanNote())
        whenever(fanNotePageRepository.findByFanNoteIdOrderByPageNumber(FanNoteId.of(7L))).thenReturn(emptyList())

        useCase.execute(7L, null)

        verify(activityEventPublisher, never()).publish(any())
    }

    private fun stubFanNote(): FanNote =
        FanNote.reconstitute(
            id = FanNoteId.of(7L),
            title = "덕질노트",
            subtitle = null,
            content = null,
            productionDate = LocalDate.now(),
            coverImageUrl = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
}

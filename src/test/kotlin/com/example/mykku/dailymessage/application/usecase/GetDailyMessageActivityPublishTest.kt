package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.application.port.output.ActivityEventPublisher
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.dailymessage.application.port.output.DailyMessageRepository
import com.example.mykku.dailymessage.domain.entity.DailyMessage
import com.example.mykku.dailymessage.domain.vo.DailyMessageId
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("하루덕담 열람 활동 이벤트 발행 단위 테스트")
class GetDailyMessageActivityPublishTest {

    @Mock
    private lateinit var dailyMessageRepository: DailyMessageRepository

    @Mock
    private lateinit var activityEventPublisher: ActivityEventPublisher

    @InjectMocks
    private lateinit var useCase: GetDailyMessageUseCaseImpl

    @Test
    @DisplayName("로그인한 회원이 열람하면 DAILYMESSAGE_VIEW 이벤트를 발행한다")
    fun publishesForLoggedInMember() {
        whenever(dailyMessageRepository.findById(DailyMessageId.of(3L))).thenReturn(stubDailyMessage())

        useCase.execute(3L, 1L)

        verify(activityEventPublisher).publish(ActivityEvent(1L, ActivityType.DAILYMESSAGE_VIEW))
    }

    @Test
    @DisplayName("비로그인 열람은 이벤트를 발행하지 않는다")
    fun doesNotPublishForAnonymous() {
        whenever(dailyMessageRepository.findById(DailyMessageId.of(3L))).thenReturn(stubDailyMessage())

        useCase.execute(3L, null)

        verify(activityEventPublisher, never()).publish(any())
    }

    private fun stubDailyMessage(): DailyMessage =
        DailyMessage.reconstitute(
            id = DailyMessageId.of(3L),
            title = "제목",
            content = "내용",
            date = LocalDate.now(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
}

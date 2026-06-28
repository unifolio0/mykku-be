package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.dto.DailyMessageResult
import com.example.mykku.dailymessage.application.port.input.GetDailyMessageUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageRepository
import com.example.mykku.dailymessage.domain.vo.DailyMessageId
import com.example.mykku.achievement.application.event.ActivityEvent
import com.example.mykku.achievement.domain.vo.ActivityType
import com.example.mykku.dailymessage.exception.DailyMessageException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetDailyMessageUseCaseImpl(
    private val dailyMessageRepository: DailyMessageRepository,
    private val eventPublisher: ApplicationEventPublisher
) : GetDailyMessageUseCase {

    override fun execute(id: Long, memberId: Long?): DailyMessageResult {
        val dailyMessage = dailyMessageRepository.findById(DailyMessageId.of(id))
            ?: throw DailyMessageException.dailyMessageNotFound()

        memberId?.let { eventPublisher.publishEvent(ActivityEvent(it, ActivityType.DAILYMESSAGE_VIEW)) }

        return DailyMessageResult.from(dailyMessage)
    }
}

package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.dto.DailyMessageSummaryResult
import com.example.mykku.dailymessage.application.port.input.GetDailyMessagesUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class GetDailyMessagesUseCaseImpl(
    private val dailyMessageRepository: DailyMessageRepository
) : GetDailyMessagesUseCase {

    override fun execute(date: LocalDate, pageable: Pageable): Page<DailyMessageSummaryResult> {
        return dailyMessageRepository.findByDateBeforeOrEqual(date, pageable)
            .map { DailyMessageSummaryResult.from(it) }
    }
}

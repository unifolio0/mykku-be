package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.dto.DailyMessageSummaryResult
import com.example.mykku.dailymessage.application.port.input.GetAllDailyMessagesUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetAllDailyMessagesUseCaseImpl(
    private val dailyMessageRepository: DailyMessageRepository
) : GetAllDailyMessagesUseCase {

    override fun execute(pageable: Pageable): Page<DailyMessageSummaryResult> {
        return dailyMessageRepository.findAll(pageable)
            .map { DailyMessageSummaryResult.from(it) }
    }
}

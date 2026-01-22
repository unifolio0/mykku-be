package com.example.mykku.dailymessage.application.port.input

import com.example.mykku.dailymessage.application.dto.DailyMessageSummaryResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetAllDailyMessagesUseCase {
    fun execute(pageable: Pageable): Page<DailyMessageSummaryResult>
}

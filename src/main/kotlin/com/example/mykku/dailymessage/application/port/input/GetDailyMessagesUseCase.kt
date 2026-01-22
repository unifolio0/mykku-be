package com.example.mykku.dailymessage.application.port.input

import com.example.mykku.dailymessage.application.dto.DailyMessageSummaryResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface GetDailyMessagesUseCase {
    fun execute(date: LocalDate, pageable: Pageable): Page<DailyMessageSummaryResult>
}

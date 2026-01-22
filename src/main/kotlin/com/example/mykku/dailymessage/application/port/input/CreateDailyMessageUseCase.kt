package com.example.mykku.dailymessage.application.port.input

import com.example.mykku.dailymessage.application.dto.CreateDailyMessageCommand
import com.example.mykku.dailymessage.application.dto.DailyMessageSummaryResult

interface CreateDailyMessageUseCase {
    fun execute(command: CreateDailyMessageCommand): DailyMessageSummaryResult
}

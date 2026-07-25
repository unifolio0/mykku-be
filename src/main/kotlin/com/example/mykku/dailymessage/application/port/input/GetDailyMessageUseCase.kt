package com.example.mykku.dailymessage.application.port.input

import com.example.mykku.dailymessage.application.dto.DailyMessageResult

interface GetDailyMessageUseCase {
    fun execute(id: Long): DailyMessageResult
}

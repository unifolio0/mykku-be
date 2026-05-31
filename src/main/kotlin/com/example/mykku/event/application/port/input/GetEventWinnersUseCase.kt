package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.EventWinnersResult

interface GetEventWinnersUseCase {
    fun execute(eventId: Long): EventWinnersResult
}

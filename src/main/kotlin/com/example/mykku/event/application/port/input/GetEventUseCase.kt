package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.EventDetailResult

interface GetEventUseCase {
    fun execute(eventId: Long, memberId: String): EventDetailResult
}

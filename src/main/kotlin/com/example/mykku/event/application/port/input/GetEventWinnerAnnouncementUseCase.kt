package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.EventWinnerAnnouncementResult

interface GetEventWinnerAnnouncementUseCase {
    fun execute(eventId: Long): EventWinnerAnnouncementResult
}

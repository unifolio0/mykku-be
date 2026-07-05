package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.EventWinnerAnnouncementResult
import com.example.mykku.event.application.dto.UpsertEventWinnerAnnouncementCommand

interface UpsertEventWinnerAnnouncementUseCase {
    fun execute(command: UpsertEventWinnerAnnouncementCommand): EventWinnerAnnouncementResult
}

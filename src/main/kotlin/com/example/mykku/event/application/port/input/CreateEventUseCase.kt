package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.CreateEventCommand
import com.example.mykku.event.application.dto.CreateEventResult

interface CreateEventUseCase {
    fun execute(command: CreateEventCommand): CreateEventResult
}

package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.EventPreviewResult

interface GetEventPreviewsUseCase {
    fun execute(): List<EventPreviewResult>
}

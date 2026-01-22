package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.EventListQuery
import com.example.mykku.event.application.dto.PagedEventsResult

interface ListEventsUseCase {
    fun execute(query: EventListQuery): PagedEventsResult
}

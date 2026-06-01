package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.GetMyAwardEventsQuery
import com.example.mykku.event.application.dto.PagedMyAwardEventsResult

interface GetMyAwardEventsUseCase {
    fun execute(query: GetMyAwardEventsQuery): PagedMyAwardEventsResult
}

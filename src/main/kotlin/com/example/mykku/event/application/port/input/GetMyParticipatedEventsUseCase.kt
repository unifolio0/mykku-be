package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.PagedEventsResult

interface GetMyParticipatedEventsUseCase {
    fun execute(memberId: String, page: Int, size: Int): PagedEventsResult
}

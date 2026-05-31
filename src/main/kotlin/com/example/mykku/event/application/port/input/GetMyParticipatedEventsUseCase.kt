package com.example.mykku.event.application.port.input

import com.example.mykku.event.application.dto.PagedMyParticipatedEventsResult

interface GetMyParticipatedEventsUseCase {
    fun execute(memberId: Long, page: Int, size: Int): PagedMyParticipatedEventsResult
}

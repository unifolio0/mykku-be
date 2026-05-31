package com.example.mykku.event.adapter.input.web

import com.example.mykku.event.application.dto.SetEventWinnersCommand
import jakarta.validation.constraints.NotEmpty

data class SetEventWinnersRequest(
    @field:NotEmpty(message = "당첨자 목록은 필수입니다")
    val participationIds: List<Long>
) {
    fun toCommand(eventId: Long): SetEventWinnersCommand {
        return SetEventWinnersCommand(
            eventId = eventId,
            participationIds = participationIds
        )
    }
}

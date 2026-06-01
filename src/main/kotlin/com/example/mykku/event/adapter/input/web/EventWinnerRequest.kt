package com.example.mykku.event.adapter.input.web

import com.example.mykku.event.application.dto.SetEventWinnersCommand
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class SetEventWinnersRequest(
    @field:NotEmpty(message = "당첨자 목록은 필수입니다")
    @field:Size(max = 1000, message = "당첨자는 한 번에 최대 1000명까지 지정할 수 있습니다")
    val participationIds: List<Long>
) {
    fun toCommand(eventId: Long): SetEventWinnersCommand {
        return SetEventWinnersCommand(
            eventId = eventId,
            participationIds = participationIds
        )
    }
}

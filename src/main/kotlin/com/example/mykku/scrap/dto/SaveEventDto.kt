package com.example.mykku.scrap.dto

import com.example.mykku.scrap.domain.SaveEvent
import org.springframework.data.domain.Page

data class SaveEventResponse(
    val id: Long,
    val eventId: Long
) {
    companion object {
        fun from(saveEvent: SaveEvent): SaveEventResponse {
            return SaveEventResponse(
                id = saveEvent.id!!,
                eventId = saveEvent.event.id!!
            )
        }

        fun fromPage(page: Page<SaveEvent>): Page<SaveEventResponse> {
            return page.map { from(it) }
        }
    }
}

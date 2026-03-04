package com.example.mykku.scrap.application.port.input

import com.example.mykku.scrap.application.dto.GetSavedEventsQuery
import com.example.mykku.scrap.application.dto.SaveEventCommand
import com.example.mykku.scrap.application.dto.SaveEventResult
import com.example.mykku.scrap.application.dto.UnsaveEventCommand
import org.springframework.data.domain.Page

interface SaveEventUseCase {
    fun saveEvent(command: SaveEventCommand)
    fun unsaveEvent(command: UnsaveEventCommand)
    fun getSavedEvents(query: GetSavedEventsQuery): Page<SaveEventResult>
    fun isSaved(memberId: Long, eventId: Long): Boolean
    fun getSavedEventIds(memberId: Long, eventIds: List<Long>): Set<Long>
}

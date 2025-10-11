package com.example.mykku.scrap.tool

import com.example.mykku.feed.domain.Event
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveEvent
import com.example.mykku.scrap.exception.ScrapException
import com.example.mykku.scrap.repository.SaveEventRepository
import org.springframework.stereotype.Component

@Component
class SaveEventWriter(
    private val saveEventRepository: SaveEventRepository,
    private val saveEventReader: SaveEventReader
) {
    fun saveEvent(member: Member, event: Event): SaveEvent {
        if (saveEventReader.isSaved(member, event)) {
            throw ScrapException.saveEventAlreadyExists()
        }

        val saveEvent = SaveEvent(
            member = member,
            event = event
        )

        return saveEventRepository.save(saveEvent)
    }

    fun unsaveEvent(member: Member, event: Event) {
        if (!saveEventReader.isSaved(member, event)) {
            throw ScrapException.saveEventNotFound()
        }

        saveEventRepository.deleteByMemberAndEvent(member, event)
    }
}

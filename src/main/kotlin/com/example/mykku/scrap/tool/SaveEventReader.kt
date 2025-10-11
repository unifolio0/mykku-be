package com.example.mykku.scrap.tool

import com.example.mykku.feed.domain.Event
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveEvent
import com.example.mykku.scrap.repository.SaveEventRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class SaveEventReader(
    private val saveEventRepository: SaveEventRepository
) {
    fun isSaved(member: Member, event: Event): Boolean {
        return saveEventRepository.existsByMemberAndEvent(member, event)
    }

    fun getSavedEvents(member: Member, pageable: Pageable): Page<SaveEvent> {
        return saveEventRepository.findByMember(member, pageable)
    }
}

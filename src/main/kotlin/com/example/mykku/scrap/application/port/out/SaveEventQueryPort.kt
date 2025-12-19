package com.example.mykku.scrap.application.port.out

import com.example.mykku.event.domain.Event
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveEvent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveEventQueryPort {
    fun isSaved(member: Member, event: Event): Boolean
    fun getSavedEvents(member: Member, pageable: Pageable): Page<SaveEvent>
    fun getSavedEventIds(member: Member, events: List<Event>): Set<Long>
}

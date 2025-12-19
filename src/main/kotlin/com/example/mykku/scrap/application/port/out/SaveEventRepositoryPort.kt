package com.example.mykku.scrap.application.port.out

import com.example.mykku.event.domain.Event
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.SaveEvent

interface SaveEventRepositoryPort {
    fun saveEvent(member: Member, event: Event): SaveEvent
    fun unsaveEvent(member: Member, event: Event)
}

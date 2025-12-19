package com.example.mykku.event.application.port.out

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventParticipation
import com.example.mykku.member.domain.Member

interface EventParticipationRepositoryPort {
    fun participate(member: Member, event: Event): EventParticipation
}

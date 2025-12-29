package com.example.mykku.event.tool

import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventParticipation
import com.example.mykku.event.exception.EventException
import com.example.mykku.event.repository.EventParticipationRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class EventParticipationWriter(
    private val eventParticipationRepository: EventParticipationRepository,
    private val eventParticipationReader: EventParticipationReader
) {
    fun participate(member: Member, event: Event): EventParticipation {
        if (eventParticipationReader.existsByMemberAndEvent(member, event)) {
            throw EventException.alreadyParticipated()
        }

        val participation = EventParticipation(
            member = member,
            event = event
        )

        return eventParticipationRepository.save(participation)
    }
}

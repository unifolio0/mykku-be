package com.example.mykku.event.infrastructure.adapter

import com.example.mykku.event.application.port.out.EventParticipationQueryPort
import com.example.mykku.event.application.port.out.EventParticipationRepositoryPort
import com.example.mykku.event.domain.Event
import com.example.mykku.event.domain.EventParticipation
import com.example.mykku.event.exception.EventException
import com.example.mykku.event.repository.EventParticipationRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class EventParticipationRepositoryAdapter(
    private val eventParticipationRepository: EventParticipationRepository,
    private val eventParticipationQueryPort: EventParticipationQueryPort
) : EventParticipationRepositoryPort {

    override fun participate(member: Member, event: Event): EventParticipation {
        if (eventParticipationQueryPort.existsByMemberAndEvent(member, event)) {
            throw EventException.alreadyParticipated()
        }

        val participation = EventParticipation(
            member = member,
            event = event
        )

        return eventParticipationRepository.save(participation)
    }
}

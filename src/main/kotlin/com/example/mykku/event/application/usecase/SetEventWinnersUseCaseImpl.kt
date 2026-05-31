package com.example.mykku.event.application.usecase

import com.example.mykku.event.application.dto.EventWinnerInfoResult
import com.example.mykku.event.application.dto.SetEventWinnersCommand
import com.example.mykku.event.application.dto.SetEventWinnersResult
import com.example.mykku.event.application.port.input.SetEventWinnersUseCase
import com.example.mykku.event.application.port.output.EventParticipationRepository
import com.example.mykku.event.application.port.output.EventRepository
import com.example.mykku.event.application.port.output.EventWinnerRepository
import com.example.mykku.event.domain.entity.Event
import com.example.mykku.event.domain.entity.EventParticipation
import com.example.mykku.event.domain.entity.EventWinner
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.domain.vo.EventParticipationId
import com.example.mykku.event.domain.vo.EventStatusType
import com.example.mykku.event.exception.EventException
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.vo.MemberPk
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SetEventWinnersUseCaseImpl(
    private val eventRepository: EventRepository,
    private val eventParticipationRepository: EventParticipationRepository,
    private val eventWinnerRepository: EventWinnerRepository,
    private val memberRepository: MemberRepository
) : SetEventWinnersUseCase {

    @Transactional
    override fun execute(command: SetEventWinnersCommand): SetEventWinnersResult {
        val eventId = EventId.of(command.eventId)
        val event = eventRepository.findById(eventId)
            ?: throw EventException.eventNotFound()

        validateEventExpired(event)
        validateParticipationIds(command.participationIds)
        val participationsMap = fetchAndValidateParticipations(eventId, command.participationIds)

        eventWinnerRepository.deleteAllByEventId(eventId)
        val savedWinners = eventWinnerRepository.saveAll(createWinners(eventId, command.participationIds))

        event.updateStatus(EventStatusType.WINNER_SELECTED)
        eventRepository.save(event)

        return buildResult(event, savedWinners, participationsMap)
    }

    private fun validateEventExpired(event: Event) {
        if (event.expiredAt.isAfter(LocalDateTime.now())) {
            throw EventException.eventNotExpired()
        }
    }

    private fun validateParticipationIds(participationIds: List<Long>) {
        if (participationIds.isEmpty()) {
            throw EventException.emptyWinners()
        }
        if (participationIds.size != participationIds.toSet().size) {
            throw EventException.duplicateWinner()
        }
    }

    private fun fetchAndValidateParticipations(
        eventId: EventId,
        participationIds: List<Long>
    ): Map<Long, EventParticipation> {
        val ids = participationIds.map { EventParticipationId.of(it) }
        val participations = eventParticipationRepository.findAllByIdIn(ids)
        if (participations.size != ids.size) {
            throw EventException.eventParticipationNotFound()
        }
        participations.forEach {
            if (it.eventId != eventId) throw EventException.participationNotBelongToEvent()
        }
        return participations.associateBy { it.id.value }
    }

    private fun createWinners(eventId: EventId, participationIds: List<Long>): List<EventWinner> {
        return participationIds.map {
            EventWinner.create(eventId = eventId, participationId = EventParticipationId.of(it))
        }
    }

    private fun buildResult(
        event: Event,
        winners: List<EventWinner>,
        participationsMap: Map<Long, EventParticipation>
    ): SetEventWinnersResult {
        val membersMap = resolveMembers(participationsMap.values)
        val winnerInfos = winners.map { winner ->
            val member = participationsMap[winner.participationId.value]?.memberId?.let { membersMap[it] }
            EventWinnerInfoResult(
                winnerId = winner.id.value,
                memberId = member?.memberId,
                nickname = member?.nickname
            )
        }
        return SetEventWinnersResult(event.id.value, event.title, winnerInfos)
    }

    private fun resolveMembers(participations: Collection<EventParticipation>) =
        memberRepository.findByIds(participations.mapNotNull { it.memberId }.map { MemberPk.of(it) })
            .associateBy { it.id.value }
}

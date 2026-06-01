package com.example.mykku.event.application.usecase

import com.example.mykku.event.application.dto.EventWinnerResult
import com.example.mykku.event.application.dto.EventWinnersResult
import com.example.mykku.event.application.port.input.GetEventWinnersUseCase
import com.example.mykku.event.application.port.output.EventParticipationRepository
import com.example.mykku.event.application.port.output.EventRepository
import com.example.mykku.event.application.port.output.EventWinnerRepository
import com.example.mykku.event.domain.entity.EventParticipation
import com.example.mykku.event.domain.entity.EventWinner
import com.example.mykku.event.domain.vo.EventId
import com.example.mykku.event.exception.EventException
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberPk
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetEventWinnersUseCaseImpl(
    private val eventRepository: EventRepository,
    private val eventWinnerRepository: EventWinnerRepository,
    private val eventParticipationRepository: EventParticipationRepository,
    private val memberRepository: MemberRepository
) : GetEventWinnersUseCase {

    @Transactional(readOnly = true)
    override fun execute(eventId: Long): EventWinnersResult {
        val event = eventRepository.findById(EventId.of(eventId))
            ?: throw EventException.eventNotFound()

        val winners = eventWinnerRepository.findByEventId(event.id)
        if (winners.isEmpty()) {
            return EventWinnersResult(event.id.value, event.title, emptyList())
        }

        val participationsMap = eventParticipationRepository
            .findAllByIdIn(winners.map { it.participationId })
            .associateBy { it.id.value }
        val membersMap = resolveMembers(participationsMap.values)

        val winnerResults = winners.map { toEventWinnerResult(it, participationsMap, membersMap) }
        return EventWinnersResult(event.id.value, event.title, winnerResults)
    }

    private fun toEventWinnerResult(
        winner: EventWinner,
        participationsMap: Map<Long, EventParticipation>,
        membersMap: Map<Long, Member>
    ): EventWinnerResult {
        val member = participationsMap[winner.participationId.value]?.memberId?.let { membersMap[it] }
        return EventWinnerResult(
            winnerId = winner.id.value,
            memberId = member?.memberId,
            nickname = member?.nickname,
            profileImage = member?.profileImage ?: ""
        )
    }

    private fun resolveMembers(participations: Collection<EventParticipation>) =
        memberRepository.findByIds(participations.mapNotNull { it.memberId }.map { MemberPk.of(it) })
            .associateBy { it.id.value }
}

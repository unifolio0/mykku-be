package com.example.mykku.contest.tool

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.contest.repository.ContestParticipationRepository
import org.springframework.data.repository.findByIdOrNull
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ContestParticipationReader(
    private val contestParticipationRepository: ContestParticipationRepository
) {
    fun existsByMemberAndContest(member: Member, contest: Contest): Boolean {
        return contestParticipationRepository.existsByMemberAndContest(member, contest)
    }

    fun existsByMemberAndContestAndFeed(member: Member, contest: Contest, feed: Feed): Boolean {
        return contestParticipationRepository.existsByMemberAndContestAndFeed(member, contest, feed)
    }

    fun getParticipationsByContest(contest: Contest, pageable: Pageable): Page<ContestParticipation> {
        return contestParticipationRepository.findByContest(contest, pageable)
    }

    fun countByContest(contest: Contest): Long {
        return contestParticipationRepository.countByContest(contest)
    }

    fun getParticipatedContestIds(member: Member, contests: List<Contest>): Set<Long> {
        return contestParticipationRepository.findByMemberAndContestIn(member, contests)
            .mapNotNull { it.contest.id }
            .toSet()
    }

    fun getParticipatedContests(member: Member, pageable: Pageable): Page<Contest> {
        return contestParticipationRepository.findContestsByMember(member, pageable)
    }

    fun getParticipationById(participationId: Long): ContestParticipation {
        return contestParticipationRepository.findByIdOrNull(participationId)
            ?: throw ContestException.participationNotFound()
    }


    fun getParticipationsByFeed(feed: Feed): List<ContestParticipation> {
        return contestParticipationRepository.findByFeed(feed)
    }

    fun getParticipationsByIds(ids: List<Long>): Map<Long, ContestParticipation> {
        if (ids.isEmpty()) return emptyMap()
        val participations = contestParticipationRepository.findAllByIdIn(ids)
        val foundIds = participations.mapNotNull { it.id }.toSet()
        val missingIds = ids.filter { it !in foundIds }
        if (missingIds.isNotEmpty()) {
            throw ContestException.participationNotFound()
        }
        return participations.associateBy { it.id!! }
    }
}

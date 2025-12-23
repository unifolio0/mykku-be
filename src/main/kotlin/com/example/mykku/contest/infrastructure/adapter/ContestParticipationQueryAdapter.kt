package com.example.mykku.contest.infrastructure.adapter

import com.example.mykku.contest.application.port.out.ContestParticipationQueryPort
import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.repository.ContestParticipationRepository
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ContestParticipationQueryAdapter(
    private val contestParticipationRepository: ContestParticipationRepository
) : ContestParticipationQueryPort {

    override fun existsByMemberAndContest(member: Member, contest: Contest): Boolean {
        return contestParticipationRepository.existsByMemberAndContest(member, contest)
    }

    override fun existsByMemberAndContestAndFeed(member: Member, contest: Contest, feed: Feed): Boolean {
        return contestParticipationRepository.existsByMemberAndContestAndFeed(member, contest, feed)
    }

    override fun getParticipationsByContest(contest: Contest, pageable: Pageable): Page<ContestParticipation> {
        return contestParticipationRepository.findByContest(contest, pageable)
    }

    override fun countByContest(contest: Contest): Long {
        return contestParticipationRepository.countByContest(contest)
    }

    override fun getParticipatedContestIds(member: Member, contests: List<Contest>): Set<Long> {
        return contestParticipationRepository.findByMemberAndContestIn(member, contests)
            .mapNotNull { it.contest.id }
            .toSet()
    }

    override fun getParticipatedContests(member: Member, pageable: Pageable): Page<Contest> {
        return contestParticipationRepository.findContestsByMember(member, pageable)
    }
}

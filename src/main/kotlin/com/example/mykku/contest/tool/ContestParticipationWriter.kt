package com.example.mykku.contest.tool

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.contest.exception.ContestException
import com.example.mykku.contest.repository.ContestParticipationRepository
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class ContestParticipationWriter(
    private val contestParticipationRepository: ContestParticipationRepository,
    private val contestParticipationReader: ContestParticipationReader
) {
    fun participateViaFeed(member: Member, contest: Contest, feed: Feed): ContestParticipation {
        if (contestParticipationReader.existsByMemberAndContestAndFeed(member, contest, feed)) {
            throw ContestException.alreadyParticipatedWithFeed()
        }

        val participation = ContestParticipation(
            member = member,
            contest = contest,
            feed = feed
        )

        return contestParticipationRepository.save(participation)
    }
}

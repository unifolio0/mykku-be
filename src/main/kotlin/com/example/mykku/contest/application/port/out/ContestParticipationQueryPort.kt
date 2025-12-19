package com.example.mykku.contest.application.port.out

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ContestParticipationQueryPort {
    fun existsByMemberAndContest(member: Member, contest: Contest): Boolean
    fun existsByMemberAndContestAndFeed(member: Member, contest: Contest, feed: Feed): Boolean
    fun getParticipationsByContest(contest: Contest, pageable: Pageable): Page<ContestParticipation>
    fun countByContest(contest: Contest): Long
    fun getParticipatedContestIds(member: Member, contests: List<Contest>): Set<Long>
    fun getParticipatedContests(member: Member, pageable: Pageable): Page<Contest>
}

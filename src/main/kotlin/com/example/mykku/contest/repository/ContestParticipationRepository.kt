package com.example.mykku.contest.repository

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestParticipation
import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ContestParticipationRepository : JpaRepository<ContestParticipation, Long> {
    fun existsByMemberAndContest(member: Member, contest: Contest): Boolean
    fun existsByMemberAndContestAndFeed(member: Member, contest: Contest, feed: Feed): Boolean
    fun findByContest(contest: Contest, pageable: Pageable): Page<ContestParticipation>
    fun countByContest(contest: Contest): Long
    fun findByMemberAndContestIn(member: Member, contests: List<Contest>): List<ContestParticipation>

    @Query("SELECT cp.contest FROM ContestParticipation cp WHERE cp.member = :member ORDER BY cp.createdAt DESC")
    fun findContestsByMember(member: Member, pageable: Pageable): Page<Contest>

    fun findByFeed(feed: Feed): List<ContestParticipation>
    fun findAllByIdIn(ids: List<Long>): List<ContestParticipation>
}

package com.example.mykku.contest.adapter.output.persistence.repository

import com.example.mykku.contest.adapter.output.persistence.entity.ContestJpaEntity
import com.example.mykku.contest.adapter.output.persistence.entity.ContestParticipationJpaEntity
import com.example.mykku.feed.adapter.output.persistence.entity.FeedJpaEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ContestParticipationJpaRepository : JpaRepository<ContestParticipationJpaEntity, Long> {
    fun existsByMemberAndContest(member: MemberJpaEntity, contest: ContestJpaEntity): Boolean
    fun existsByMemberAndContestAndFeed(member: MemberJpaEntity, contest: ContestJpaEntity, feed: FeedJpaEntity): Boolean
    fun findByContest(contest: ContestJpaEntity, pageable: Pageable): Page<ContestParticipationJpaEntity>
    fun countByContest(contest: ContestJpaEntity): Long
    fun findByMemberAndContestIn(member: MemberJpaEntity, contests: List<ContestJpaEntity>): List<ContestParticipationJpaEntity>

    @Query("SELECT cp.contest FROM ContestParticipationJpaEntity cp WHERE cp.member = :member ORDER BY cp.createdAt DESC")
    fun findContestsByMember(member: MemberJpaEntity, pageable: Pageable): Page<ContestJpaEntity>

    fun findByFeed(feed: FeedJpaEntity): List<ContestParticipationJpaEntity>
    fun findAllByIdIn(ids: List<Long>): List<ContestParticipationJpaEntity>
}

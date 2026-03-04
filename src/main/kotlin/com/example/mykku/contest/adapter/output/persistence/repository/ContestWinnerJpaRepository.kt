package com.example.mykku.contest.adapter.output.persistence.repository

import com.example.mykku.contest.adapter.output.persistence.entity.ContestJpaEntity
import com.example.mykku.contest.adapter.output.persistence.entity.ContestParticipationJpaEntity
import com.example.mykku.contest.adapter.output.persistence.entity.ContestWinnerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ContestWinnerJpaRepository : JpaRepository<ContestWinnerJpaEntity, Long> {
    fun findByContest(contest: ContestJpaEntity): List<ContestWinnerJpaEntity>
    fun findByContestIn(contests: List<ContestJpaEntity>): List<ContestWinnerJpaEntity>
    fun existsByContest(contest: ContestJpaEntity): Boolean
    fun deleteAllByContest(contest: ContestJpaEntity)
    fun deleteAllByParticipationIn(participations: List<ContestParticipationJpaEntity>)

    @Query(
        "SELECT w FROM ContestWinnerJpaEntity w " +
        "JOIN w.participation p " +
        "WHERE w.contest = :contest AND p.member.id = :memberId"
    )
    fun findByContestAndMemberId(
        @Param("contest") contest: ContestJpaEntity,
        @Param("memberId") memberId: Long
    ): ContestWinnerJpaEntity?
}

package com.example.mykku.contest.repository

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestWinner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContestWinnerRepository : JpaRepository<ContestWinner, Long> {
    fun findByContest(contest: Contest): List<ContestWinner>
    fun findByContestIn(contests: List<Contest>): List<ContestWinner>
    fun existsByContest(contest: Contest): Boolean
    fun deleteAllByContest(contest: Contest)
}

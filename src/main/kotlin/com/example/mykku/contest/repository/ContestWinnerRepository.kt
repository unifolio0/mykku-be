package com.example.mykku.contest.repository

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestWinner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContestWinnerRepository : JpaRepository<ContestWinner, Long> {
    fun findByContestIn(contests: List<Contest>): List<ContestWinner>
    fun findByContest(contest: Contest): List<ContestWinner>
    fun findByContestOrderByWinnerRankAsc(contest: Contest): List<ContestWinner>
}

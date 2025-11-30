package com.example.mykku.contest.repository

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContestImageRepository : JpaRepository<ContestImage, Long> {
    fun findByContestIn(contests: List<Contest>): List<ContestImage>
    fun findByContest(contest: Contest): List<ContestImage>
}

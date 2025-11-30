package com.example.mykku.contest.repository

import com.example.mykku.contest.domain.Contest
import com.example.mykku.contest.domain.ContestTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContestTagRepository : JpaRepository<ContestTag, Long> {
    fun findAllByTitleIn(titles: Collection<String>): List<ContestTag>
    fun findByContestIn(contests: List<Contest>): List<ContestTag>
    fun findByContest(contest: Contest): List<ContestTag>
}

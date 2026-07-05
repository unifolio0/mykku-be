package com.example.mykku.contest.adapter.output.persistence.repository

import com.example.mykku.contest.adapter.output.persistence.entity.ContestJpaEntity
import com.example.mykku.contest.adapter.output.persistence.entity.ContestWinnerAnnouncementJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContestWinnerAnnouncementJpaRepository : JpaRepository<ContestWinnerAnnouncementJpaEntity, Long> {
    fun findByContest(contest: ContestJpaEntity): ContestWinnerAnnouncementJpaEntity?
}

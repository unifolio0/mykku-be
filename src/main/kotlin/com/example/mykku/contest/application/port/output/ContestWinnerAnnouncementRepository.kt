package com.example.mykku.contest.application.port.output

import com.example.mykku.contest.domain.entity.ContestWinnerAnnouncement
import com.example.mykku.contest.domain.vo.ContestId

interface ContestWinnerAnnouncementRepository {
    fun save(announcement: ContestWinnerAnnouncement): ContestWinnerAnnouncement
    fun findByContestId(contestId: ContestId): ContestWinnerAnnouncement?
}

package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.ContestWinnerAnnouncementResult

interface GetContestWinnerAnnouncementUseCase {
    fun execute(contestId: Long): ContestWinnerAnnouncementResult
}

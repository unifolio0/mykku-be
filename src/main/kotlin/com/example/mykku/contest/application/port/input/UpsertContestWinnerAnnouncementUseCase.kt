package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.ContestWinnerAnnouncementResult
import com.example.mykku.contest.application.dto.UpsertContestWinnerAnnouncementCommand

interface UpsertContestWinnerAnnouncementUseCase {
    fun execute(command: UpsertContestWinnerAnnouncementCommand): ContestWinnerAnnouncementResult
}

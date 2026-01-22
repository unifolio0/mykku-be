package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.ContestWinnerDetailResult

interface GetContestWinnerDetailUseCase {
    fun execute(contestId: Long): ContestWinnerDetailResult
}

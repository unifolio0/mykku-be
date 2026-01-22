package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.ContestDetailResult

interface GetContestUseCase {
    fun execute(contestId: Long, memberId: String): ContestDetailResult
}

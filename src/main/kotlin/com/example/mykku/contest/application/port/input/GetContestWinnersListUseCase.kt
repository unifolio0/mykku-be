package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.ContestWinnersListResult

interface GetContestWinnersListUseCase {
    fun execute(): ContestWinnersListResult
}

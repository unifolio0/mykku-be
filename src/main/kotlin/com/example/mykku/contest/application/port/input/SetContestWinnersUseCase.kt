package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.SetContestWinnersCommand
import com.example.mykku.contest.application.dto.SetContestWinnersResult

interface SetContestWinnersUseCase {
    fun execute(command: SetContestWinnersCommand): SetContestWinnersResult
}

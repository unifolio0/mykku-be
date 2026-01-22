package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.CreateContestCommand
import com.example.mykku.contest.application.dto.CreateContestResult

interface CreateContestUseCase {
    fun execute(command: CreateContestCommand): CreateContestResult
}

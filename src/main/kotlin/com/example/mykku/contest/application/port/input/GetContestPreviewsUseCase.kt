package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.ContestPreviewResult

interface GetContestPreviewsUseCase {
    fun execute(): List<ContestPreviewResult>
}

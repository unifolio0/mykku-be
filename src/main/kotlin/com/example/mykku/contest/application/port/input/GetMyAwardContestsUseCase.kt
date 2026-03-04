package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.GetMyAwardsQuery
import com.example.mykku.contest.application.dto.PagedMyAwardsResult

interface GetMyAwardContestsUseCase {
    fun execute(query: GetMyAwardsQuery): PagedMyAwardsResult
}

package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.ContestListQuery
import com.example.mykku.contest.application.dto.PagedContestsResult

interface ListContestsUseCase {
    fun execute(query: ContestListQuery): PagedContestsResult
}

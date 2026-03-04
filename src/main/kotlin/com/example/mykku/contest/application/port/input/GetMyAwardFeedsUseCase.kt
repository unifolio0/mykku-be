package com.example.mykku.contest.application.port.input

import com.example.mykku.contest.application.dto.GetMyAwardFeedsQuery
import com.example.mykku.feed.application.dto.PagedFeedsResult

interface GetMyAwardFeedsUseCase {
    fun execute(query: GetMyAwardFeedsQuery): PagedFeedsResult
}

package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.GetPopularFeedsQuery
import com.example.mykku.feed.application.dto.PopularFeedsResult

interface GetPopularFeedsUseCase {
    fun execute(query: GetPopularFeedsQuery): PopularFeedsResult
}

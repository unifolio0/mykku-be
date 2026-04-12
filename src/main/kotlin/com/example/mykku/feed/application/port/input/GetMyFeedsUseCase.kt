package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.GetMyFeedsQuery
import com.example.mykku.feed.application.dto.PagedFeedsResult

interface GetMyFeedsUseCase {
    fun execute(query: GetMyFeedsQuery): PagedFeedsResult
}

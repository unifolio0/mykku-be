package com.example.mykku.feed.application.port.input

import com.example.mykku.feed.application.dto.ListFeedsQuery
import com.example.mykku.feed.application.dto.PagedFeedsResult

interface ListFeedsUseCase {
    fun execute(query: ListFeedsQuery): PagedFeedsResult
}

package com.example.mykku.block.application.port.input

import com.example.mykku.block.application.dto.GetKeywordBlocksQuery
import com.example.mykku.block.application.dto.KeywordBlockListResult

interface GetKeywordBlocksUseCase {
    fun getKeywordBlocks(query: GetKeywordBlocksQuery): KeywordBlockListResult
}

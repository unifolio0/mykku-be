package com.example.mykku.block.application.port.input

import com.example.mykku.block.application.dto.BlockKeywordCommand
import com.example.mykku.block.application.dto.KeywordBlockResult

interface BlockKeywordUseCase {
    fun blockKeyword(command: BlockKeywordCommand): KeywordBlockResult
}

package com.example.mykku.block.application.port.input

import com.example.mykku.block.application.dto.UnblockKeywordCommand

interface UnblockKeywordUseCase {
    fun unblockKeyword(command: UnblockKeywordCommand)
}

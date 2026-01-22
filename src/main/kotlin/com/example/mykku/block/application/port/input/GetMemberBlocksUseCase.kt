package com.example.mykku.block.application.port.input

import com.example.mykku.block.application.dto.GetMemberBlocksQuery
import com.example.mykku.block.application.dto.MemberBlockListResult

interface GetMemberBlocksUseCase {
    fun getMemberBlocks(query: GetMemberBlocksQuery): MemberBlockListResult
}

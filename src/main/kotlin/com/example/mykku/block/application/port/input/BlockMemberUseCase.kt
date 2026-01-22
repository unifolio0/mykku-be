package com.example.mykku.block.application.port.input

import com.example.mykku.block.application.dto.BlockMemberCommand
import com.example.mykku.block.application.dto.MemberBlockResult

interface BlockMemberUseCase {
    fun blockMember(command: BlockMemberCommand): MemberBlockResult
}

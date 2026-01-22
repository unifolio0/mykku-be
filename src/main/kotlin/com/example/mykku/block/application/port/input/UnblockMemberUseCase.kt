package com.example.mykku.block.application.port.input

import com.example.mykku.block.application.dto.UnblockMemberCommand

interface UnblockMemberUseCase {
    fun unblockMember(command: UnblockMemberCommand)
}

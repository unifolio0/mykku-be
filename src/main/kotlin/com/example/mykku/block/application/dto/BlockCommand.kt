package com.example.mykku.block.application.dto

data class BlockMemberCommand(
    val blockerId: Long,
    val blockedMemberId: String
)

data class UnblockMemberCommand(
    val blockerId: Long,
    val blockedMemberId: String
)

data class BlockKeywordCommand(
    val memberId: Long,
    val keyword: String
)

data class UnblockKeywordCommand(
    val memberId: Long,
    val keyword: String
)

package com.example.mykku.block.application.dto

data class BlockMemberCommand(
    val blockerId: String,
    val blockedMemberId: String
)

data class UnblockMemberCommand(
    val blockerId: String,
    val blockedMemberId: String
)

data class BlockKeywordCommand(
    val memberId: String,
    val keyword: String
)

data class UnblockKeywordCommand(
    val memberId: String,
    val keyword: String
)

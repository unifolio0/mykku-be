package com.example.mykku.block.application.dto

import org.springframework.data.domain.Pageable

data class GetMemberBlocksQuery(
    val memberId: String,
    val pageable: Pageable
)

data class GetKeywordBlocksQuery(
    val memberId: String,
    val pageable: Pageable
)

data class GetBlockedMemberIdsQuery(
    val memberId: String
)

data class GetBlockedKeywordsQuery(
    val memberId: String
)

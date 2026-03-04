package com.example.mykku.block.application.dto

import org.springframework.data.domain.Pageable

data class GetMemberBlocksQuery(
    val memberId: Long,
    val pageable: Pageable
)

data class GetKeywordBlocksQuery(
    val memberId: Long,
    val pageable: Pageable
)

data class GetBlockedMemberIdsQuery(
    val memberId: Long
)

data class GetBlockedKeywordsQuery(
    val memberId: Long
)

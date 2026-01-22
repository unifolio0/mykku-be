package com.example.mykku.feed.application.dto

import org.springframework.data.domain.Pageable

data class ListFeedsQuery(
    val boardId: Long,
    val memberId: String?,
    val pageable: Pageable
)

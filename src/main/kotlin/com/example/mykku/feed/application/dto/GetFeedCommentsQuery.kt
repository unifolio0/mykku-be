package com.example.mykku.feed.application.dto

import org.springframework.data.domain.Pageable

data class GetFeedCommentsQuery(
    val feedId: Long,
    val memberId: Long?,
    val pageable: Pageable
)

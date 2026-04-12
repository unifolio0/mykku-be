package com.example.mykku.feed.application.dto

import org.springframework.data.domain.Pageable

data class GetMyFeedsQuery(
    val memberId: Long,
    val pageable: Pageable
)

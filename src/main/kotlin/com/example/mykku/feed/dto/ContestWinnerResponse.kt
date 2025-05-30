package com.example.mykku.feed.dto

import java.util.*

data class ContestWinnerResponse(
    val id: UUID,
    val image: String,
    val rank: Int,
)

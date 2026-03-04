package com.example.mykku.like.application.dto

data class LikeFeedCommand(
    val memberId: Long,
    val feedId: Long
)

data class UnlikeFeedCommand(
    val memberId: Long,
    val feedId: Long
)

package com.example.mykku.like.application.dto

data class LikeFeedCommand(
    val memberId: String,
    val feedId: Long
)

data class UnlikeFeedCommand(
    val memberId: String,
    val feedId: Long
)

package com.example.mykku.feed.application.dto

data class DeleteFeedCommand(
    val feedId: Long,
    val memberId: Long
)

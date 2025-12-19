package com.example.mykku.feed.application.port.out

import com.example.mykku.feed.domain.model.FeedId

data class FeedSummary(
    val id: Long,
    val title: String,
    val authorId: String,
    val boardId: Long
)

interface FeedQueryPort {
    fun findSummaryById(id: FeedId): FeedSummary?
    fun existsById(id: FeedId): Boolean
}

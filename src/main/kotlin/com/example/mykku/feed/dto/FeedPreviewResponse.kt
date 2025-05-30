package com.example.mykku.feed.dto

import com.example.mykku.feed.domain.Feed
import java.util.*

data class FeedPreviewResponse(
    val id: Long,
    val board: String,
    val title: String,
    val content: String,
    val likeCount: Int,
    val commentCount: Int,
) {
    constructor(feed: Feed) : this(
        id = feed.id!!,
        board = feed.board.title,
        title = feed.title,
        content = feed.content,
        likeCount = feed.likeCount,
        commentCount = feed.commentCount,
    )
}

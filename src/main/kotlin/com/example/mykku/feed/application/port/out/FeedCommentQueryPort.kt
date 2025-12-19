package com.example.mykku.feed.application.port.out

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FeedCommentQueryPort {
    fun getFeedCommentById(id: Long): FeedComment
    fun getCommentsByFeed(feed: Feed, pageable: Pageable): Page<FeedComment>
    fun getRepliesByParentComment(parentComment: FeedComment): List<FeedComment>
    fun getRepliesByParentComments(parentComments: List<FeedComment>): Map<Long, List<FeedComment>>
    fun getCommentCount(feed: Feed): Long
}

package com.example.mykku.feed.infrastructure.adapter

import com.example.mykku.feed.application.port.out.FeedCommentQueryPort
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.repository.FeedCommentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class FeedCommentQueryAdapter(
    private val feedCommentRepository: FeedCommentRepository
) : FeedCommentQueryPort {

    override fun getFeedCommentById(id: Long): FeedComment {
        return feedCommentRepository.findByIdOrNull(id)
            ?: throw FeedException.feedCommentNotFound()
    }

    override fun getCommentsByFeed(feed: Feed, pageable: Pageable): Page<FeedComment> {
        return feedCommentRepository.findByFeedAndParentCommentIsNull(feed, pageable)
    }

    override fun getRepliesByParentComment(parentComment: FeedComment): List<FeedComment> {
        return feedCommentRepository.findByParentComment(parentComment)
    }

    override fun getRepliesByParentComments(parentComments: List<FeedComment>): Map<Long, List<FeedComment>> {
        if (parentComments.isEmpty()) return emptyMap()

        val allReplies = feedCommentRepository.findByParentCommentIn(parentComments)
        return allReplies.groupBy { it.parentComment?.id ?: 0L }
    }

    override fun getCommentCount(feed: Feed): Long {
        return feedCommentRepository.countByFeed(feed)
    }
}

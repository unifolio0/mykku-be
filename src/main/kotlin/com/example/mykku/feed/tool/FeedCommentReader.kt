package com.example.mykku.feed.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.repository.FeedCommentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class FeedCommentReader(
    private val feedCommentRepository: FeedCommentRepository
) {
    fun getFeedCommentById(id: Long): FeedComment {
        return feedCommentRepository.findByIdOrNull(id)
            ?: throw MykkuException(ErrorCode.FEED_COMMENT_NOT_FOUND)
    }
    
    fun getCommentsByFeed(feed: Feed, pageable: Pageable): Page<FeedComment> {
        return feedCommentRepository.findByFeedAndParentCommentIsNull(feed, pageable)
    }
    
    fun getRepliesByParentComment(parentComment: FeedComment): List<FeedComment> {
        return feedCommentRepository.findByParentComment(parentComment)
    }
    
    fun getRepliesByParentComments(parentComments: List<FeedComment>): Map<Long, List<FeedComment>> {
        if (parentComments.isEmpty()) return emptyMap()
        
        val allReplies = feedCommentRepository.findByParentCommentIn(parentComments)
        return allReplies.groupBy { it.parentComment?.id ?: 0L }
    }
    
    fun getCommentCount(feed: Feed): Long {
        return feedCommentRepository.countByFeed(feed)
    }
}
package com.example.mykku.feed.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.repository.FeedCommentRepository
import org.springframework.stereotype.Component

@Component
class FeedCommentReader(
    private val feedCommentRepository: FeedCommentRepository
) {
    fun getFeedCommentById(feedCommentId: Long): FeedComment {
        return feedCommentRepository.findById(feedCommentId)
            .orElseThrow { MykkuException(ErrorCode.FEED_COMMENT_NOT_FOUND) }
    }
}

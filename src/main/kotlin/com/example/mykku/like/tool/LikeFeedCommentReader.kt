package com.example.mykku.like.tool

import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.repository.LikeFeedCommentRepository
import org.springframework.stereotype.Component

@Component
class LikeFeedCommentReader(
    private val likeFeedCommentRepository: LikeFeedCommentRepository
) {
    fun validateLikeFeedCommentNotExists(memberId: String, feedCommentId: Long) {
        if (likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)) {
            throw MykkuException(ErrorCode.LIKE_FEED_COMMENT_ALREADY_LIKED)
        }
    }

    fun validateLikeFeedCommentExists(memberId: String, feedCommentId: Long) {
        if (!likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)) {
            throw MykkuException(ErrorCode.LIKE_FEED_COMMENT_NOT_FOUND)
        }
    }
    
    fun isLiked(memberId: String, feedComment: FeedComment): Boolean {
        return likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedComment.id!!)
    }
}

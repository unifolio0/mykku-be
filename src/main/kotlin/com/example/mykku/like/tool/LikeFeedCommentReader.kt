package com.example.mykku.like.tool

import com.example.mykku.like.exception.LikeException
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.repository.LikeFeedCommentRepository
import org.springframework.stereotype.Component

@Component
class LikeFeedCommentReader(
    private val likeFeedCommentRepository: LikeFeedCommentRepository
) {
    fun validateLikeFeedCommentNotExists(memberId: String, feedCommentId: Long) {
        if (likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)) {
            throw LikeException.likeFeedCommentAlreadyLiked()
        }
    }

    fun validateLikeFeedCommentExists(memberId: String, feedCommentId: Long) {
        if (!likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)) {
            throw LikeException.likeFeedCommentNotFound()
        }
    }
    
    fun isLiked(memberId: String, feedComment: FeedComment): Boolean {
        return likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedComment.id!!)
    }
}

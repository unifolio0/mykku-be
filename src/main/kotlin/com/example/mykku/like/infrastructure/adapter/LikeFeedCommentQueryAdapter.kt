package com.example.mykku.like.infrastructure.adapter

import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.application.port.out.LikeFeedCommentQueryPort
import com.example.mykku.like.exception.LikeException
import com.example.mykku.like.repository.LikeFeedCommentRepository
import org.springframework.stereotype.Component

@Component
class LikeFeedCommentQueryAdapter(
    private val likeFeedCommentRepository: LikeFeedCommentRepository
) : LikeFeedCommentQueryPort {

    override fun validateLikeFeedCommentNotExists(memberId: String, feedCommentId: Long) {
        if (likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)) {
            throw LikeException.likeFeedCommentAlreadyLiked()
        }
    }

    override fun validateLikeFeedCommentExists(memberId: String, feedCommentId: Long) {
        if (!likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedCommentId)) {
            throw LikeException.likeFeedCommentNotFound()
        }
    }

    override fun isLiked(memberId: String, feedComment: FeedComment): Boolean {
        return likeFeedCommentRepository.existsByMemberIdAndFeedCommentId(memberId, feedComment.id!!)
    }
}

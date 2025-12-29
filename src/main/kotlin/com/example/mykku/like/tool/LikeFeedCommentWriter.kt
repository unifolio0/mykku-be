package com.example.mykku.like.tool

import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.domain.LikeFeedComment
import com.example.mykku.like.repository.LikeFeedCommentRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class LikeFeedCommentWriter(
    private val likeFeedCommentRepository: LikeFeedCommentRepository
) {
    fun createLikeFeedComment(feedComment: FeedComment, member: Member): LikeFeedComment {
        val likeFeedComment = LikeFeedComment(
            member = member,
            feedComment = feedComment
        )
        return likeFeedCommentRepository.save(likeFeedComment)
    }

    fun deleteLikeFeedComment(memberId: String, feedCommentId: Long) {
        likeFeedCommentRepository.deleteByMemberIdAndFeedCommentId(memberId, feedCommentId)
    }
}

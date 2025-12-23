package com.example.mykku.like.application.port.out

import com.example.mykku.feed.domain.FeedComment

interface LikeFeedCommentQueryPort {
    fun validateLikeFeedCommentNotExists(memberId: String, feedCommentId: Long)
    fun validateLikeFeedCommentExists(memberId: String, feedCommentId: Long)
    fun isLiked(memberId: String, feedComment: FeedComment): Boolean
}

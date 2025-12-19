package com.example.mykku.like.application.port.out

import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.like.domain.LikeFeedComment
import com.example.mykku.member.domain.Member

interface LikeFeedCommentRepositoryPort {
    fun createLikeFeedComment(feedComment: FeedComment, member: Member): LikeFeedComment
    fun deleteLikeFeedComment(memberId: String, feedCommentId: Long)
}

package com.example.mykku.feed.application.port.out

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.member.domain.Member

interface FeedCommentRepositoryPort {
    fun createComment(
        content: String,
        feed: Feed,
        member: Member,
        parentComment: FeedComment? = null
    ): FeedComment

    fun updateComment(
        comment: FeedComment,
        newContent: String
    ): FeedComment

    fun deleteComment(comment: FeedComment)
}

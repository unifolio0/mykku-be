package com.example.mykku.feed.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.repository.FeedCommentRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class FeedCommentWriter(
    private val feedCommentRepository: FeedCommentRepository,
) {
    fun createComment(
        content: String,
        feed: Feed,
        member: Member,
        parentComment: FeedComment? = null,
    ): FeedComment {
        val comment = FeedComment(
            content = content,
            feed = feed,
            member = member,
            parentComment = parentComment,
        )

        return feedCommentRepository.save(comment)
    }

    fun updateComment(
        comment: FeedComment,
        newContent: String,
    ): FeedComment {
        comment.updateContent(newContent)
        return feedCommentRepository.save(comment)
    }

    fun deleteComment(comment: FeedComment) {
        feedCommentRepository.delete(comment)
    }
}

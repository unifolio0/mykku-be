package com.example.mykku.feed.infrastructure.adapter

import com.example.mykku.feed.application.port.out.FeedCommentRepositoryPort
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.repository.FeedCommentRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class FeedCommentRepositoryAdapter(
    private val feedCommentRepository: FeedCommentRepository
) : FeedCommentRepositoryPort {

    override fun createComment(
        content: String,
        feed: Feed,
        member: Member,
        parentComment: FeedComment?
    ): FeedComment {
        val comment = FeedComment(
            content = content,
            feed = feed,
            member = member,
            parentComment = parentComment,
        )
        return feedCommentRepository.save(comment)
    }

    override fun updateComment(
        comment: FeedComment,
        newContent: String
    ): FeedComment {
        comment.updateContent(newContent)
        return feedCommentRepository.save(comment)
    }

    override fun deleteComment(comment: FeedComment) {
        feedCommentRepository.delete(comment)
    }
}

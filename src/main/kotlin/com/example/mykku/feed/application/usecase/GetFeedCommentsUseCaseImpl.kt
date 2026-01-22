package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.application.dto.CommentAuthorResult
import com.example.mykku.feed.application.dto.FeedCommentReplyResult
import com.example.mykku.feed.application.dto.FeedCommentResult
import com.example.mykku.feed.application.dto.FeedCommentsResult
import com.example.mykku.feed.application.dto.GetFeedCommentsQuery
import com.example.mykku.feed.application.port.input.GetFeedCommentsUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.like.tool.LikeFeedCommentReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFeedCommentsUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val likeFeedCommentReader: LikeFeedCommentReader
) : GetFeedCommentsUseCase {

    override fun execute(query: GetFeedCommentsQuery): FeedCommentsResult {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(query.feedId))
        val commentsPage = feedCommentRepository.findByFeedAndParentCommentIsNull(feed, query.pageable)

        val repliesMap = if (commentsPage.content.isNotEmpty()) {
            feedCommentRepository.findByParentCommentIn(commentsPage.content)
                .groupBy { it.parentComment?.id ?: 0L }
        } else {
            emptyMap()
        }

        val commentResponses = commentsPage.content.map { comment ->
            val replies = repliesMap[comment.id] ?: emptyList()
            val legacyComment = createLegacyComment(comment)

            val replyResponses = replies.map { reply ->
                val legacyReply = createLegacyComment(reply)
                FeedCommentReplyResult(
                    id = reply.id!!,
                    content = reply.content,
                    author = CommentAuthorResult(
                        memberId = reply.member.memberId,
                        nickname = reply.member.nickname,
                        profileImage = reply.member.profileImage
                    ),
                    likeCount = reply.likeCount,
                    isLiked = query.memberId?.let { likeFeedCommentReader.isLiked(it, legacyReply) } ?: false,
                    createdAt = reply.createdAt,
                    updatedAt = reply.updatedAt
                )
            }

            FeedCommentResult(
                id = comment.id!!,
                content = comment.content,
                author = CommentAuthorResult(
                    memberId = comment.member.memberId,
                    nickname = comment.member.nickname,
                    profileImage = comment.member.profileImage
                ),
                likeCount = comment.likeCount,
                isLiked = query.memberId?.let { likeFeedCommentReader.isLiked(it, legacyComment) } ?: false,
                replies = replyResponses,
                replyCount = replyResponses.size,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }

        return FeedCommentsResult(
            comments = commentResponses,
            totalElements = commentsPage.totalElements,
            totalPages = commentsPage.totalPages,
            currentPage = commentsPage.number,
            pageSize = commentsPage.size,
            hasNext = commentsPage.hasNext()
        )
    }

    private fun createLegacyComment(
        comment: com.example.mykku.feed.adapter.output.persistence.entity.FeedCommentJpaEntity
    ): com.example.mykku.feed.domain.FeedComment {
        val legacyFeed = com.example.mykku.feed.domain.Feed(
            id = comment.feed.id,
            title = comment.feed.title,
            content = comment.feed.content,
            likeCount = comment.feed.likeCount,
            commentCount = comment.feed.commentCount,
            board = comment.feed.board,
            member = comment.feed.member
        )

        return com.example.mykku.feed.domain.FeedComment(
            id = comment.id,
            content = comment.content,
            likeCount = comment.likeCount,
            feed = legacyFeed,
            parentComment = null,
            member = comment.member
        )
    }
}

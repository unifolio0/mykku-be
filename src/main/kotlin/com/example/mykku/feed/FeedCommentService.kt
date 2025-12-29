package com.example.mykku.feed

import com.example.mykku.feed.dto.CommentAuthorResponse
import com.example.mykku.feed.dto.CreateFeedCommentRequest
import com.example.mykku.feed.dto.FeedCommentReplyResponse
import com.example.mykku.feed.dto.FeedCommentResponse
import com.example.mykku.feed.dto.FeedCommentsResponse
import com.example.mykku.feed.dto.SingleFeedCommentResponse
import com.example.mykku.feed.dto.UpdateFeedCommentRequest
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.tool.FeedCommentReader
import com.example.mykku.feed.tool.FeedCommentWriter
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.like.tool.LikeFeedCommentReader
import com.example.mykku.member.tool.MemberReader
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedCommentService(
    private val feedReader: FeedReader,
    private val feedCommentReader: FeedCommentReader,
    private val feedCommentWriter: FeedCommentWriter,
    private val likeFeedCommentReader: LikeFeedCommentReader,
    private val memberReader: MemberReader,
) {
    @Transactional(readOnly = true)
    fun getComments(feedId: Long, memberId: String?, pageable: Pageable): FeedCommentsResponse {
        val feed = feedReader.getFeedById(feedId)
        val commentsPage = feedCommentReader.getCommentsByFeed(feed, pageable)

        val repliesMap = feedCommentReader.getRepliesByParentComments(commentsPage.content)

        val commentResponses = commentsPage.content.map { comment ->
            val replies = repliesMap[comment.id] ?: emptyList()
            val replyResponses = replies.map { reply ->
                FeedCommentReplyResponse(
                    id = reply.id!!,
                    content = reply.content,
                    author = CommentAuthorResponse(
                        memberId = reply.member.id,
                        nickname = reply.member.nickname,
                        profileImage = reply.member.profileImage
                    ),
                    likeCount = reply.likeCount,
                    isLiked = memberId?.let { likeFeedCommentReader.isLiked(it, reply) } ?: false,
                    createdAt = reply.createdAt,
                    updatedAt = reply.updatedAt
                )
            }

            FeedCommentResponse(
                id = comment.id!!,
                content = comment.content,
                author = CommentAuthorResponse(
                    memberId = comment.member.id,
                    nickname = comment.member.nickname,
                    profileImage = comment.member.profileImage
                ),
                likeCount = comment.likeCount,
                isLiked = memberId?.let { likeFeedCommentReader.isLiked(it, comment) } ?: false,
                replies = replyResponses,
                replyCount = replyResponses.size,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }

        return FeedCommentsResponse(
            comments = commentResponses,
            totalElements = commentsPage.totalElements,
            totalPages = commentsPage.totalPages,
            currentPage = commentsPage.number,
            pageSize = commentsPage.size,
            hasNext = commentsPage.hasNext()
        )
    }

    @Transactional
    fun createComment(
        feedId: Long,
        memberId: String,
        request: CreateFeedCommentRequest,
    ): SingleFeedCommentResponse {
        val feed = feedReader.getFeedById(feedId)
        val member = memberReader.getMemberById(memberId)

        val parentComment = request.parentCommentId?.let { parentId ->
            feedCommentReader.getFeedCommentById(parentId)
        }

        val comment = feedCommentWriter.createComment(
            content = request.content,
            feed = feed,
            member = member,
            parentComment = parentComment,
        )

        return SingleFeedCommentResponse(
            id = comment.id!!,
            content = comment.content,
            author = CommentAuthorResponse(
                memberId = comment.member.id,
                nickname = comment.member.nickname,
                profileImage = comment.member.profileImage
            ),
            likeCount = comment.likeCount,
            createdAt = comment.createdAt,
        )
    }

    @Transactional
    fun updateComment(
        commentId: Long,
        memberId: String,
        request: UpdateFeedCommentRequest,
    ): SingleFeedCommentResponse {
        val comment = feedCommentReader.getFeedCommentById(commentId)

        if (comment.member.id != memberId) {
            throw FeedException.feedCommentForbiddenAccess()
        }

        val updatedComment = feedCommentWriter.updateComment(
            comment = comment,
            newContent = request.content,
        )

        return SingleFeedCommentResponse(
            id = updatedComment.id!!,
            content = updatedComment.content,
            author = CommentAuthorResponse(
                memberId = updatedComment.member.id,
                nickname = updatedComment.member.nickname,
                profileImage = updatedComment.member.profileImage
            ),
            likeCount = updatedComment.likeCount,
            createdAt = updatedComment.createdAt,
        )
    }

    @Transactional
    fun deleteComment(commentId: Long, memberId: String) {
        val comment = feedCommentReader.getFeedCommentById(commentId)

        if (comment.member.id != memberId) {
            throw FeedException.feedCommentForbiddenAccess()
        }

        feedCommentWriter.deleteComment(comment)
    }
}

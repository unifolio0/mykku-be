package com.example.mykku.feed

import com.example.mykku.feed.dto.CommentAuthorResponse
import com.example.mykku.feed.dto.FeedCommentReplyResponse
import com.example.mykku.feed.dto.FeedCommentResponse
import com.example.mykku.feed.dto.FeedCommentsResponse
import com.example.mykku.feed.tool.FeedCommentReader
import com.example.mykku.feed.tool.FeedReader
import com.example.mykku.like.tool.LikeFeedCommentReader
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedCommentService(
    private val feedReader: FeedReader,
    private val feedCommentReader: FeedCommentReader,
    private val likeFeedCommentReader: LikeFeedCommentReader
) {
    @Transactional(readOnly = true)
    fun getComments(feedId: Long, memberId: String?, pageable: Pageable): FeedCommentsResponse {
        val feed = feedReader.getFeedById(feedId)
        val commentsPage = feedCommentReader.getCommentsByFeed(feed, pageable)

        // 모든 부모 댓글의 대댓글을 한 번에 조회
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
}

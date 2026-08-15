package com.example.mykku.feed.application.usecase

import com.example.mykku.feed.application.dto.CommentAuthorResult
import com.example.mykku.feed.application.dto.FeedCommentReplyResult
import com.example.mykku.feed.application.dto.FeedCommentResult
import com.example.mykku.feed.application.dto.FeedCommentsResult
import com.example.mykku.feed.application.dto.GetFeedCommentsQuery
import com.example.mykku.feed.application.port.input.GetFeedCommentsUseCase
import com.example.mykku.feed.application.port.output.FeedCommentRepository
import com.example.mykku.feed.application.port.output.FeedRepository
import com.example.mykku.feed.domain.entity.FeedComment
import com.example.mykku.feed.domain.vo.FeedId
import com.example.mykku.like.application.port.output.LikeFeedCommentPort
import com.example.mykku.member.application.port.output.MemberRepository
import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.MemberPk
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetFeedCommentsUseCaseImpl(
    private val feedRepository: FeedRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val memberRepository: MemberRepository,
    private val likeFeedCommentPort: LikeFeedCommentPort
) : GetFeedCommentsUseCase {

    override fun execute(query: GetFeedCommentsQuery): FeedCommentsResult {
        val feed = feedRepository.findByIdOrThrow(FeedId.of(query.feedId))
        val commentsPage = feedCommentRepository.findByFeedIdAndParentCommentIsNull(feed.id!!, query.pageable)
        val repliesByParentId = loadReplies(commentsPage)
        val context = loadContext(commentsPage.content, repliesByParentId, query.memberId)

        return FeedCommentsResult(
            comments = commentsPage.content.map { buildCommentResult(it, repliesByParentId, context) },
            totalElements = commentsPage.totalElements,
            totalPages = commentsPage.totalPages,
            currentPage = commentsPage.number,
            pageSize = commentsPage.size,
            hasNext = commentsPage.hasNext()
        )
    }

    private fun loadReplies(commentsPage: Page<FeedComment>): Map<Long, List<FeedComment>> {
        val parentCommentIds = commentsPage.content.mapNotNull { it.id }
        if (parentCommentIds.isEmpty()) return emptyMap()
        return feedCommentRepository.findByParentCommentIds(parentCommentIds)
            .groupBy { it.parentCommentId?.value ?: 0L }
    }

    private fun loadContext(
        comments: List<FeedComment>,
        repliesByParentId: Map<Long, List<FeedComment>>,
        viewerId: Long?
    ): CommentAssemblyContext {
        val allComments = comments + repliesByParentId.values.flatten()
        val commentIds = allComments.mapNotNull { it.id?.value }

        return CommentAssemblyContext(
            authorsById = loadAuthors(allComments),
            likeCountByCommentId = likeFeedCommentPort.countByFeedCommentIdIn(commentIds),
            likedCommentIds = loadLikedCommentIds(viewerId, commentIds)
        )
    }

    private fun buildCommentResult(
        comment: FeedComment,
        repliesByParentId: Map<Long, List<FeedComment>>,
        context: CommentAssemblyContext
    ): FeedCommentResult {
        val commentId = comment.id!!.value
        val replies = repliesByParentId[commentId] ?: emptyList()

        return FeedCommentResult(
            id = commentId,
            content = comment.content,
            author = buildAuthorResult(comment, context),
            likeCount = context.likeCountByCommentId[commentId] ?: 0,
            isLiked = commentId in context.likedCommentIds,
            replies = replies.map { buildReplyResult(it, context) },
            replyCount = replies.size,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt
        )
    }

    private fun buildReplyResult(reply: FeedComment, context: CommentAssemblyContext): FeedCommentReplyResult {
        val replyId = reply.id!!.value
        return FeedCommentReplyResult(
            id = replyId,
            content = reply.content,
            author = buildAuthorResult(reply, context),
            likeCount = context.likeCountByCommentId[replyId] ?: 0,
            isLiked = replyId in context.likedCommentIds,
            createdAt = reply.createdAt,
            updatedAt = reply.updatedAt
        )
    }

    private fun buildAuthorResult(comment: FeedComment, context: CommentAssemblyContext): CommentAuthorResult? {
        val member = comment.memberId?.let { context.authorsById[it] } ?: return null
        return CommentAuthorResult(
            memberId = member.memberId,
            nickname = member.nickname,
            profileImage = member.profileImage
        )
    }

    private fun loadAuthors(comments: List<FeedComment>): Map<Long, Member> {
        val memberPks = comments.mapNotNull { it.memberId }.distinct().map { MemberPk.of(it) }
        if (memberPks.isEmpty()) return emptyMap()
        return memberRepository.findByIds(memberPks).associateBy { it.id.value }
    }

    private fun loadLikedCommentIds(viewerId: Long?, commentIds: List<Long>): Set<Long> {
        if (viewerId == null) return emptySet()
        return likeFeedCommentPort.findLikedFeedCommentIds(viewerId, commentIds)
    }

    private data class CommentAssemblyContext(
        val authorsById: Map<Long, Member>,
        val likeCountByCommentId: Map<Long, Int>,
        val likedCommentIds: Set<Long>
    )
}

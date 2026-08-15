package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.dto.CommentAuthorInfo
import com.example.mykku.dailymessage.application.dto.CommentResult
import com.example.mykku.dailymessage.application.dto.DailyMessageCommentsResult
import com.example.mykku.dailymessage.application.dto.ReplyResult
import com.example.mykku.dailymessage.application.port.input.GetCommentsUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageCommentRepository
import com.example.mykku.dailymessage.application.port.output.DailyMessageRepository
import com.example.mykku.dailymessage.domain.entity.DailyMessageComment
import com.example.mykku.dailymessage.domain.vo.DailyMessageId
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.like.application.port.output.LikeDailyMessageCommentPort
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetCommentsUseCaseImpl(
    private val dailyMessageRepository: DailyMessageRepository,
    private val dailyMessageCommentRepository: DailyMessageCommentRepository,
    private val likeDailyMessageCommentPort: LikeDailyMessageCommentPort,
    private val commentAuthorResolver: CommentAuthorResolver
) : GetCommentsUseCase {

    override fun execute(dailyMessageId: Long, memberId: Long?, pageable: Pageable): DailyMessageCommentsResult {
        val id = DailyMessageId.of(dailyMessageId)

        dailyMessageRepository.findById(id)
            ?: throw DailyMessageException.dailyMessageNotFound()

        val commentsPage = dailyMessageCommentRepository
            .findByDailyMessageIdAndParentCommentIsNull(id, pageable)

        val parentCommentIds = commentsPage.content.map { it.id.value }
        val replies = if (parentCommentIds.isNotEmpty()) {
            dailyMessageCommentRepository.findByParentCommentIds(parentCommentIds)
        } else {
            emptyList()
        }

        val likedIds = resolveLikedIds(memberId, commentsPage.content, replies)
        val authors = commentAuthorResolver.resolve(
            (commentsPage.content + replies).mapNotNull { it.memberId }
        )
        val repliesMap = replies.groupBy { it.parentCommentId }
        val commentResults = commentsPage.content.map { comment ->
            toCommentResult(comment, repliesMap[comment.id.value] ?: emptyList(), likedIds, authors)
        }

        return DailyMessageCommentsResult(
            comments = commentResults,
            totalElements = commentsPage.totalElements,
            totalPages = commentsPage.totalPages,
            currentPage = commentsPage.number,
            pageSize = commentsPage.size,
            hasNext = commentsPage.hasNext()
        )
    }

    private fun resolveLikedIds(
        memberId: Long?,
        comments: List<DailyMessageComment>,
        replies: List<DailyMessageComment>
    ): Set<Long> {
        if (memberId == null) {
            return emptySet()
        }
        val allCommentIds = comments.map { it.id.value } + replies.map { it.id.value }
        return likeDailyMessageCommentPort.findLikedCommentIds(memberId, allCommentIds)
    }

    private fun toCommentResult(
        comment: DailyMessageComment,
        replies: List<DailyMessageComment>,
        likedIds: Set<Long>,
        authors: Map<Long, CommentAuthorInfo>
    ): CommentResult {
        val replyResults = replies.map {
            ReplyResult.from(it, authorOf(it, authors), likedIds.contains(it.id.value))
        }
        return CommentResult.from(
            comment,
            authorOf(comment, authors),
            likedIds.contains(comment.id.value),
            replyResults
        )
    }

    private fun authorOf(
        comment: DailyMessageComment,
        authors: Map<Long, CommentAuthorInfo>
    ): CommentAuthorInfo? {
        return comment.memberId?.let { authors[it] }
    }
}

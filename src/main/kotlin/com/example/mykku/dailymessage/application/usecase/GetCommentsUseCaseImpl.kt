package com.example.mykku.dailymessage.application.usecase

import com.example.mykku.dailymessage.application.dto.CommentResult
import com.example.mykku.dailymessage.application.dto.DailyMessageCommentsResult
import com.example.mykku.dailymessage.application.dto.ReplyResult
import com.example.mykku.dailymessage.application.port.input.GetCommentsUseCase
import com.example.mykku.dailymessage.application.port.output.DailyMessageCommentRepository
import com.example.mykku.dailymessage.application.port.output.DailyMessageRepository
import com.example.mykku.dailymessage.domain.vo.DailyMessageId
import com.example.mykku.dailymessage.exception.DailyMessageException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetCommentsUseCaseImpl(
    private val dailyMessageRepository: DailyMessageRepository,
    private val dailyMessageCommentRepository: DailyMessageCommentRepository
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

        val repliesMap = replies.groupBy { it.parentCommentId }

        val commentResults = commentsPage.content.map { comment ->
            val commentReplies = repliesMap[comment.id.value] ?: emptyList()
            val replyResults = commentReplies.map { ReplyResult.from(it) }
            CommentResult.from(comment, replyResults)
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
}

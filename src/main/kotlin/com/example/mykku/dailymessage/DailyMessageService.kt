package com.example.mykku.dailymessage

import com.example.mykku.dailymessage.application.port.out.DailyMessageQueryPort
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.domain.SortDirection
import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.DailyMessageResponse
import com.example.mykku.dailymessage.dto.DailyMessageSummaryResponse
import com.example.mykku.dailymessage.dto.ReplyResponse
import java.time.LocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyMessageService(
    private val dailyMessageQueryPort: DailyMessageQueryPort
) {
    @Transactional(readOnly = true)
    fun getDailyMessages(date: LocalDate, limit: Int, sort: SortDirection): List<DailyMessageSummaryResponse> {
        val dailyMessages = dailyMessageQueryPort.getDailyMessages(date, limit, sort)

        return dailyMessages.map { it.toResponse() }
    }

    private fun DailyMessage.toResponse(): DailyMessageSummaryResponse {
        return DailyMessageSummaryResponse(
            id = this.id!!,
            title = this.title,
            content = this.content,
            date = this.date
        )
    }

    @Transactional(readOnly = true)
    fun getDailyMessage(id: Long): DailyMessageResponse {
        val dailyMessage = dailyMessageQueryPort.getDailyMessage(id)
        val allComments = dailyMessageQueryPort.getCommentsByDailyMessage(dailyMessage)
        val repliesByParentId = getReplies(allComments)
        val comments = getCommentResponses(allComments, repliesByParentId)

        return DailyMessageResponse(
            id = dailyMessage.id!!,
            title = dailyMessage.title,
            content = dailyMessage.content,
            createdAt = dailyMessage.createdAt,
            comments = comments
        )
    }

    private fun getReplies(allComments: List<DailyMessageComment>): Map<Long?, List<ReplyResponse>> {
        val repliesByParentId = allComments
            .filter { it.parentComment != null }
            .groupBy { it.parentComment!!.id }
            .mapValues { (_, replies) ->
                replies.map { reply ->
                    ReplyResponse(
                        id = reply.id!!,
                        content = reply.content,
                        likeCount = reply.likeCount,
                        memberName = reply.member.nickname,
                        profileImage = reply.member.profileImage,
                        createdAt = reply.createdAt
                    )
                }
            }
        return repliesByParentId
    }

    private fun getCommentResponses(
        allComments: List<DailyMessageComment>,
        repliesByParentId: Map<Long?, List<ReplyResponse>>
    ): List<CommentResponse> {
        val comments = allComments
            .filter { it.parentComment == null }
            .map { comment ->
                CommentResponse(
                    id = comment.id!!,
                    content = comment.content,
                    likeCount = comment.likeCount,
                    memberName = comment.member.nickname,
                    profileImage = comment.member.profileImage,
                    createdAt = comment.createdAt,
                    replies = repliesByParentId[comment.id] ?: emptyList()
                )
            }
        return comments
    }
}

package com.example.mykku.dailymessage.application.dto

import com.example.mykku.dailymessage.domain.entity.DailyMessage
import com.example.mykku.dailymessage.domain.entity.DailyMessageComment
import java.time.LocalDate
import java.time.LocalDateTime

data class DailyMessageSummaryResult(
    val id: Long,
    val title: String,
    val content: String,
    val date: LocalDate
) {
    companion object {
        fun from(dailyMessage: DailyMessage): DailyMessageSummaryResult {
            return DailyMessageSummaryResult(
                id = dailyMessage.id.value,
                title = dailyMessage.title,
                content = dailyMessage.content,
                date = dailyMessage.date
            )
        }
    }
}

data class DailyMessageResult(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(dailyMessage: DailyMessage): DailyMessageResult {
            return DailyMessageResult(
                id = dailyMessage.id.value,
                title = dailyMessage.title,
                content = dailyMessage.content,
                createdAt = dailyMessage.createdAt
            )
        }
    }
}

data class CommentResult(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val memberName: String?,
    val profileImage: String,
    val createdAt: LocalDateTime,
    val replies: List<ReplyResult>
) {
    companion object {
        fun from(comment: DailyMessageComment, replies: List<ReplyResult> = emptyList()): CommentResult {
            return CommentResult(
                id = comment.id.value,
                content = comment.content,
                likeCount = comment.likeCount,
                memberName = comment.memberNickname,
                profileImage = comment.memberProfileImage,
                createdAt = comment.createdAt,
                replies = replies
            )
        }
    }
}

data class ReplyResult(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val memberName: String?,
    val profileImage: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(reply: DailyMessageComment): ReplyResult {
            return ReplyResult(
                id = reply.id.value,
                content = reply.content,
                likeCount = reply.likeCount,
                memberName = reply.memberNickname,
                profileImage = reply.memberProfileImage,
                createdAt = reply.createdAt
            )
        }
    }
}

data class DailyMessageCommentsResult(
    val comments: List<CommentResult>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean
)

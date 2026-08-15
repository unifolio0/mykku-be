package com.example.mykku.dailymessage.application.dto

import com.example.mykku.dailymessage.domain.entity.DailyMessage
import com.example.mykku.dailymessage.domain.entity.DailyMessageComment
import com.example.mykku.role.application.dto.RoleResult
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
    val date: LocalDate,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(dailyMessage: DailyMessage): DailyMessageResult {
            return DailyMessageResult(
                id = dailyMessage.id.value,
                title = dailyMessage.title,
                content = dailyMessage.content,
                date = dailyMessage.date,
                createdAt = dailyMessage.createdAt
            )
        }
    }
}

data class CommentAuthorInfo(
    val memberId: String?,
    val role: RoleResult?
)

data class CommentResult(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val memberId: String?,
    val memberName: String?,
    val role: RoleResult?,
    val profileImage: String?,
    val createdAt: LocalDateTime,
    val replies: List<ReplyResult>
) {
    companion object {
        fun from(
            comment: DailyMessageComment,
            author: CommentAuthorInfo?,
            isLiked: Boolean = false,
            replies: List<ReplyResult> = emptyList()
        ): CommentResult {
            return CommentResult(
                id = comment.id.value,
                content = comment.content,
                likeCount = comment.likeCount,
                isLiked = isLiked,
                memberId = author?.memberId,
                memberName = comment.memberNickname,
                role = author?.role,
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
    val isLiked: Boolean,
    val memberId: String?,
    val memberName: String?,
    val role: RoleResult?,
    val profileImage: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(
            reply: DailyMessageComment,
            author: CommentAuthorInfo?,
            isLiked: Boolean = false
        ): ReplyResult {
            return ReplyResult(
                id = reply.id.value,
                content = reply.content,
                likeCount = reply.likeCount,
                isLiked = isLiked,
                memberId = author?.memberId,
                memberName = reply.memberNickname,
                role = author?.role,
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

package com.example.mykku.dailymessage.adapter.input.web

import com.example.mykku.dailymessage.application.dto.CommentResult
import com.example.mykku.dailymessage.application.dto.DailyMessageCommentsResult
import com.example.mykku.dailymessage.application.dto.DailyMessageResult
import com.example.mykku.dailymessage.application.dto.DailyMessageSummaryResult
import com.example.mykku.dailymessage.application.dto.ReplyResult
import com.example.mykku.role.adapter.input.web.RoleResponse
import java.time.LocalDate
import java.time.LocalDateTime

data class DailyMessageSummaryResponse(
    val id: Long,
    val title: String,
    val content: String,
    val date: LocalDate
) {
    companion object {
        fun from(result: DailyMessageSummaryResult): DailyMessageSummaryResponse {
            return DailyMessageSummaryResponse(
                id = result.id,
                title = result.title,
                content = result.content,
                date = result.date
            )
        }
    }
}

data class DailyMessageResponse(
    val id: Long,
    val title: String,
    val content: String,
    val date: LocalDate,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: DailyMessageResult): DailyMessageResponse {
            return DailyMessageResponse(
                id = result.id,
                title = result.title,
                content = result.content,
                date = result.date,
                createdAt = result.createdAt
            )
        }
    }
}

data class CommentResponse(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val memberId: String?,
    val memberName: String?,
    val role: RoleResponse?,
    val profileImage: String?,
    val createdAt: LocalDateTime,
    val replies: List<ReplyResponse>
) {
    companion object {
        fun from(result: CommentResult): CommentResponse {
            return CommentResponse(
                id = result.id,
                content = result.content,
                likeCount = result.likeCount,
                isLiked = result.isLiked,
                memberId = result.memberId,
                memberName = result.memberName,
                role = result.role?.let { RoleResponse(it.id, it.name, it.description) },
                profileImage = result.profileImage,
                createdAt = result.createdAt,
                replies = result.replies.map { ReplyResponse.from(it) }
            )
        }
    }
}

data class ReplyResponse(
    val id: Long,
    val content: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val memberId: String?,
    val memberName: String?,
    val role: RoleResponse?,
    val profileImage: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(result: ReplyResult): ReplyResponse {
            return ReplyResponse(
                id = result.id,
                content = result.content,
                likeCount = result.likeCount,
                isLiked = result.isLiked,
                memberId = result.memberId,
                memberName = result.memberName,
                role = result.role?.let { RoleResponse(it.id, it.name, it.description) },
                profileImage = result.profileImage,
                createdAt = result.createdAt
            )
        }
    }
}

data class DailyMessageCommentsResponse(
    val comments: List<CommentResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean
) {
    companion object {
        fun from(result: DailyMessageCommentsResult): DailyMessageCommentsResponse {
            return DailyMessageCommentsResponse(
                comments = result.comments.map { CommentResponse.from(it) },
                totalElements = result.totalElements,
                totalPages = result.totalPages,
                currentPage = result.currentPage,
                pageSize = result.pageSize,
                hasNext = result.hasNext
            )
        }
    }
}

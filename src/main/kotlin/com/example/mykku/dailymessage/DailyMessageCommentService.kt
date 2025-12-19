package com.example.mykku.dailymessage

import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
import com.example.mykku.dailymessage.tool.DailyMessageCommentReader
import com.example.mykku.dailymessage.tool.DailyMessageCommentWriter
import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.member.application.port.out.MemberQueryPort
import com.example.mykku.member.domain.model.MemberId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyMessageCommentService(
    private val dailyMessageReader: DailyMessageReader,
    private val dailyMessageCommentReader: DailyMessageCommentReader,
    private val dailyMessageCommentWriter: DailyMessageCommentWriter,
    private val memberQueryPort: MemberQueryPort,
) {
    @Transactional
    fun createComment(
        dailyMessageId: Long,
        memberId: String,
        request: CreateCommentRequest,
    ): CommentResponse {
        val dailyMessage = dailyMessageReader.getDailyMessage(dailyMessageId)
        val member = memberQueryPort.getMemberById(MemberId(memberId))

        val parentComment = request.parentCommentId?.let { parentId ->
            dailyMessageCommentReader.getCommentByDailyMessageId(parentId, dailyMessageId)
        }

        val comment = dailyMessageCommentWriter.createComment(
            content = request.content,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = parentComment,
        )

        return CommentResponse(
            id = comment.id!!,
            content = comment.content,
            likeCount = comment.likeCount,
            memberName = comment.member.nickname,
            profileImage = comment.member.profileImage,
            createdAt = comment.createdAt,
            replies = emptyList(),
        )
    }

    @Transactional
    fun updateComment(
        commentId: Long,
        memberId: String,
        request: UpdateCommentRequest,
    ): CommentResponse {
        val comment = dailyMessageCommentReader.getComment(commentId)

        if (comment.member.id != memberId) {
            throw DailyMessageException.commentForbiddenAccess()
        }

        val updatedComment = dailyMessageCommentWriter.updateComment(
            comment = comment,
            newContent = request.content,
        )

        return CommentResponse(
            id = updatedComment.id!!,
            content = updatedComment.content,
            likeCount = updatedComment.likeCount,
            memberName = updatedComment.member.nickname,
            profileImage = updatedComment.member.profileImage,
            createdAt = updatedComment.createdAt,
            replies = emptyList(),
        )
    }

    @Transactional
    fun deleteComment(commentId: Long, memberId: String) {
        val comment = dailyMessageCommentReader.getComment(commentId)

        if (comment.member.id != memberId) {
            throw DailyMessageException.commentForbiddenAccess()
        }

        dailyMessageCommentWriter.deleteComment(comment)
    }
}

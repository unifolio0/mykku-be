package com.example.mykku.dailymessage

import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
import com.example.mykku.dailymessage.tool.DailyMessageCommentReader
import com.example.mykku.dailymessage.tool.DailyMessageCommentWriter
import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.member.tool.MemberReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyMessageCommentService(
    private val dailyMessageReader: DailyMessageReader,
    private val dailyMessageCommentReader: DailyMessageCommentReader,
    private val dailyMessageCommentWriter: DailyMessageCommentWriter,
    private val memberReader: MemberReader,
) {
    @Transactional
    fun createComment(
        dailyMessageId: Long,
        memberId: String,
        request: CreateCommentRequest,
    ): CommentResponse {
        val dailyMessage = dailyMessageReader.getDailyMessage(dailyMessageId)
        val member = memberReader.getMemberById(memberId)

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
            memberName = comment.member.nickname,
            memberProfileImage = comment.member.profileImage,
            createdAt = comment.createdAt,
            likeCount = comment.likeCount,
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
            memberName = updatedComment.member.nickname,
            memberProfileImage = updatedComment.member.profileImage,
            createdAt = updatedComment.createdAt,
            likeCount = updatedComment.likeCount,
            replies = emptyList(),
        )
    }
}

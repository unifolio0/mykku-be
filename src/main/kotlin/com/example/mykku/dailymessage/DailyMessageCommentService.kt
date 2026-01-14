package com.example.mykku.dailymessage

import com.example.mykku.block.tool.BlockFilterHelper
import com.example.mykku.dailymessage.dto.CommentResponse
import com.example.mykku.dailymessage.dto.CreateCommentRequest
import com.example.mykku.dailymessage.dto.DailyMessageCommentsResponse
import com.example.mykku.dailymessage.dto.ReplyResponse
import com.example.mykku.dailymessage.dto.UpdateCommentRequest
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.dailymessage.tool.DailyMessageCommentReader
import com.example.mykku.dailymessage.tool.DailyMessageCommentWriter
import com.example.mykku.dailymessage.tool.DailyMessageReader
import com.example.mykku.member.tool.MemberReader
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyMessageCommentService(
    private val dailyMessageReader: DailyMessageReader,
    private val dailyMessageCommentReader: DailyMessageCommentReader,
    private val dailyMessageCommentWriter: DailyMessageCommentWriter,
    private val memberReader: MemberReader,
    private val blockFilterHelper: BlockFilterHelper
) {
    @Transactional(readOnly = true)
    fun getComments(dailyMessageId: Long, memberId: String?, pageable: Pageable): DailyMessageCommentsResponse {
        dailyMessageReader.getDailyMessage(dailyMessageId)

        val commentsPage = dailyMessageCommentReader.getCommentsByDailyMessageId(dailyMessageId, pageable)

        val filteredComments = blockFilterHelper.filterContent(
            items = commentsPage.content,
            memberId = memberId,
            memberIdExtractor = { it.member.id },
            contentExtractors = listOf({ it.content })
        )

        val repliesMap = dailyMessageCommentReader.getRepliesByParentComments(filteredComments)

        val commentResponses = filteredComments.map { comment ->
            val replies = repliesMap[comment.id] ?: emptyList()

            val filteredReplies = blockFilterHelper.filterContent(
                items = replies,
                memberId = memberId,
                memberIdExtractor = { it.member.id },
                contentExtractors = listOf({ it.content })
            )

            val replyResponses = filteredReplies.map { reply ->
                ReplyResponse(
                    id = reply.id!!,
                    content = reply.content,
                    likeCount = reply.likeCount,
                    memberName = reply.member.nickname,
                    profileImage = reply.member.profileImage,
                    createdAt = reply.createdAt
                )
            }

            CommentResponse(
                id = comment.id!!,
                content = comment.content,
                likeCount = comment.likeCount,
                memberName = comment.member.nickname,
                profileImage = comment.member.profileImage,
                createdAt = comment.createdAt,
                replies = replyResponses
            )
        }

        return DailyMessageCommentsResponse(
            comments = commentResponses,
            totalElements = commentsPage.totalElements,
            totalPages = commentsPage.totalPages,
            currentPage = commentsPage.number,
            pageSize = commentsPage.size,
            hasNext = commentsPage.hasNext()
        )
    }

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

package com.example.mykku.dailymessage.adapter.input.web

import com.example.mykku.dailymessage.application.dto.CreateCommentCommand
import com.example.mykku.dailymessage.application.dto.UpdateCommentCommand

data class CreateCommentRequest(
    val content: String,
    val parentCommentId: Long? = null
) {
    fun toCommand(
        dailyMessageId: Long,
        memberId: String,
        memberNickname: String?,
        memberProfileImage: String
    ): CreateCommentCommand {
        return CreateCommentCommand(
            dailyMessageId = dailyMessageId,
            memberId = memberId,
            memberNickname = memberNickname,
            memberProfileImage = memberProfileImage,
            content = content,
            parentCommentId = parentCommentId
        )
    }
}

data class UpdateCommentRequest(
    val content: String
) {
    fun toCommand(commentId: Long, memberId: String): UpdateCommentCommand {
        return UpdateCommentCommand(
            commentId = commentId,
            memberId = memberId,
            content = content
        )
    }
}

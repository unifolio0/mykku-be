package com.example.mykku.dailymessage.application.port.out

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.member.domain.Member

interface DailyMessageCommentRepositoryPort {
    fun createComment(
        content: String,
        dailyMessage: DailyMessage,
        member: Member,
        parentComment: DailyMessageComment? = null
    ): DailyMessageComment

    fun updateComment(comment: DailyMessageComment, newContent: String): DailyMessageComment
    fun deleteComment(comment: DailyMessageComment)
}

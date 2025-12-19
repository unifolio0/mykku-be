package com.example.mykku.dailymessage.infrastructure.adapter

import com.example.mykku.dailymessage.application.port.out.DailyMessageCommentRepositoryPort
import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class DailyMessageCommentRepositoryAdapter(
    private val dailyMessageCommentRepository: DailyMessageCommentRepository
) : DailyMessageCommentRepositoryPort {

    override fun createComment(
        content: String,
        dailyMessage: DailyMessage,
        member: Member,
        parentComment: DailyMessageComment?
    ): DailyMessageComment {
        val comment = DailyMessageComment(
            content = content,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = parentComment
        )
        return dailyMessageCommentRepository.save(comment)
    }

    override fun updateComment(comment: DailyMessageComment, newContent: String): DailyMessageComment {
        comment.updateContent(newContent)
        return dailyMessageCommentRepository.save(comment)
    }

    override fun deleteComment(comment: DailyMessageComment) {
        dailyMessageCommentRepository.delete(comment)
    }
}

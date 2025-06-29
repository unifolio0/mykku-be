package com.example.mykku.dailymessage.tool

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
import com.example.mykku.member.domain.Member
import org.springframework.stereotype.Component

@Component
class DailyMessageCommentWriter(
    private val dailyMessageCommentRepository: DailyMessageCommentRepository,
) {
    fun createComment(
        content: String,
        dailyMessage: DailyMessage,
        member: Member,
        parentComment: DailyMessageComment? = null,
    ): DailyMessageComment {
        val comment = DailyMessageComment(
            content = content,
            dailyMessage = dailyMessage,
            member = member,
            parentComment = parentComment,
        )
        
        return dailyMessageCommentRepository.save(comment)
    }
    
    fun updateComment(
        comment: DailyMessageComment,
        newContent: String,
    ): DailyMessageComment {
        comment.updateContent(newContent)
        return dailyMessageCommentRepository.save(comment)
    }
}

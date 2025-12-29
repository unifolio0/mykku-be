package com.example.mykku.dailymessage.tool

import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
import com.example.mykku.dailymessage.exception.DailyMessageException
import org.springframework.stereotype.Component

@Component
class DailyMessageCommentReader(
    private val dailyMessageCommentRepository: DailyMessageCommentRepository,
) {
    fun getComment(commentId: Long): DailyMessageComment {
        return dailyMessageCommentRepository.findById(commentId)
            .orElseThrow { DailyMessageException.dailyMessageCommentNotFound() }
    }

    fun getCommentByDailyMessageId(commentId: Long, dailyMessageId: Long): DailyMessageComment {
        return dailyMessageCommentRepository.findByIdAndDailyMessageId(commentId, dailyMessageId)
            ?: throw DailyMessageException.dailyMessageCommentNotFound()
    }

    fun getDailyMessageCommentById(dailyMessageCommentId: Long): DailyMessageComment {
        return dailyMessageCommentRepository.findById(dailyMessageCommentId)
            .orElseThrow { DailyMessageException.dailyMessageCommentNotFound() }
    }
}

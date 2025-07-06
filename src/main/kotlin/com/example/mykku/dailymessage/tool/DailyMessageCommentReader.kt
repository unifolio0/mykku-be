package com.example.mykku.dailymessage.tool

import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import org.springframework.stereotype.Component

@Component
class DailyMessageCommentReader(
    private val dailyMessageCommentRepository: DailyMessageCommentRepository,
) {
    fun getComment(commentId: Long): DailyMessageComment {
        return dailyMessageCommentRepository.findById(commentId)
            .orElseThrow { MykkuException(ErrorCode.DAILY_MESSAGE_COMMENT_NOT_FOUND) }
    }

    fun getCommentByDailyMessageId(commentId: Long, dailyMessageId: Long): DailyMessageComment {
        return dailyMessageCommentRepository.findByIdAndDailyMessageId(commentId, dailyMessageId)
            ?: throw MykkuException(ErrorCode.DAILY_MESSAGE_COMMENT_NOT_FOUND)
    }

    fun getDailyMessageCommentById(dailyMessageCommentId: Long): DailyMessageComment {
        return dailyMessageCommentRepository.findById(dailyMessageCommentId)
            .orElseThrow { MykkuException(ErrorCode.DAILY_MESSAGE_COMMENT_NOT_FOUND) }
    }
}

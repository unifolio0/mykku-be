package com.example.mykku.dailymessage.infrastructure.adapter

import com.example.mykku.dailymessage.application.port.out.DailyMessageCommentQueryPort
import com.example.mykku.dailymessage.domain.DailyMessageComment
import com.example.mykku.dailymessage.exception.DailyMessageException
import com.example.mykku.dailymessage.repository.DailyMessageCommentRepository
import org.springframework.stereotype.Component

@Component
class DailyMessageCommentQueryAdapter(
    private val dailyMessageCommentRepository: DailyMessageCommentRepository
) : DailyMessageCommentQueryPort {

    override fun getComment(commentId: Long): DailyMessageComment {
        return dailyMessageCommentRepository.findById(commentId)
            .orElseThrow { DailyMessageException.dailyMessageCommentNotFound() }
    }

    override fun getCommentByDailyMessageId(commentId: Long, dailyMessageId: Long): DailyMessageComment {
        return dailyMessageCommentRepository.findByIdAndDailyMessageId(commentId, dailyMessageId)
            ?: throw DailyMessageException.dailyMessageCommentNotFound()
    }

    override fun getDailyMessageCommentById(dailyMessageCommentId: Long): DailyMessageComment {
        return dailyMessageCommentRepository.findById(dailyMessageCommentId)
            .orElseThrow { DailyMessageException.dailyMessageCommentNotFound() }
    }
}

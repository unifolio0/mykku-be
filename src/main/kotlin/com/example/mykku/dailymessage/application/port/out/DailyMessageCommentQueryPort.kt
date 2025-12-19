package com.example.mykku.dailymessage.application.port.out

import com.example.mykku.dailymessage.domain.DailyMessageComment

interface DailyMessageCommentQueryPort {
    fun getComment(commentId: Long): DailyMessageComment
    fun getCommentByDailyMessageId(commentId: Long, dailyMessageId: Long): DailyMessageComment
    fun getDailyMessageCommentById(dailyMessageCommentId: Long): DailyMessageComment
}

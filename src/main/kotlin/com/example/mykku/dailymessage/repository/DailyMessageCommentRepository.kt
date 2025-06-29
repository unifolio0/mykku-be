package com.example.mykku.dailymessage.repository

import com.example.mykku.dailymessage.domain.DailyMessageComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DailyMessageCommentRepository : JpaRepository<DailyMessageComment, Long> {
    @Query("SELECT c FROM DailyMessageComment c WHERE c.id = :commentId AND c.dailyMessage.id = :dailyMessageId")
    fun findByIdAndDailyMessageId(commentId: Long, dailyMessageId: Long): DailyMessageComment?
}

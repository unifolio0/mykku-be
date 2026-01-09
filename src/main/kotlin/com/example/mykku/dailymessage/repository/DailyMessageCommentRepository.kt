package com.example.mykku.dailymessage.repository

import com.example.mykku.dailymessage.domain.DailyMessage
import com.example.mykku.dailymessage.domain.DailyMessageComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DailyMessageCommentRepository : JpaRepository<DailyMessageComment, Long> {
    @Query("SELECT c FROM DailyMessageComment c WHERE c.id = :commentId AND c.dailyMessage.id = :dailyMessageId")
    fun findByIdAndDailyMessageId(commentId: Long, dailyMessageId: Long): DailyMessageComment?

    @Query("SELECT c FROM DailyMessageComment c JOIN FETCH c.member WHERE c.dailyMessage = :dailyMessage")
    fun findByDailyMessage(dailyMessage: DailyMessage): List<DailyMessageComment>

    @Query(
        value = "SELECT c FROM DailyMessageComment c JOIN FETCH c.member WHERE c.dailyMessage.id = :dailyMessageId AND c.parentComment IS NULL",
        countQuery = "SELECT COUNT(c) FROM DailyMessageComment c WHERE c.dailyMessage.id = :dailyMessageId AND c.parentComment IS NULL"
    )
    fun findByDailyMessageIdAndParentCommentIsNull(
        dailyMessageId: Long,
        pageable: Pageable
    ): Page<DailyMessageComment>

    @Query("SELECT c FROM DailyMessageComment c JOIN FETCH c.member WHERE c.parentComment IN :parentComments")
    fun findByParentCommentIn(parentComments: List<DailyMessageComment>): List<DailyMessageComment>
}

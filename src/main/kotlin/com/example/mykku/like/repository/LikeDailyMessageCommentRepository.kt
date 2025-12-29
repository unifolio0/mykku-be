package com.example.mykku.like.repository

import com.example.mykku.like.domain.LikeDailyMessageComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeDailyMessageCommentRepository : JpaRepository<LikeDailyMessageComment, Long> {
    fun existsByMemberIdAndDailyMessageCommentId(memberId: String, dailyMessageCommentId: Long): Boolean

    fun deleteByMemberIdAndDailyMessageCommentId(memberId: String, dailyMessageCommentId: Long)
}

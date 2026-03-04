package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.like.adapter.output.persistence.entity.LikeDailyMessageCommentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeDailyMessageCommentJpaRepository : JpaRepository<LikeDailyMessageCommentJpaEntity, Long> {
    fun existsByMemberIdAndDailyMessageCommentId(memberId: Long, dailyMessageCommentId: Long): Boolean
    fun deleteByMemberIdAndDailyMessageCommentId(memberId: Long, dailyMessageCommentId: Long)
}

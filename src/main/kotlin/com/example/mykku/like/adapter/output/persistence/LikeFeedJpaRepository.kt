package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.like.adapter.output.persistence.entity.LikeFeedJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeFeedJpaRepository : JpaRepository<LikeFeedJpaEntity, Long> {
    fun existsByMemberIdAndFeedId(memberId: Long, feedId: Long): Boolean
    fun deleteByMemberIdAndFeedId(memberId: Long, feedId: Long)
    fun findByMemberIdAndFeedIdIn(memberId: Long, feedIds: List<Long>): List<LikeFeedJpaEntity>
    fun deleteAllByFeedId(feedId: Long)
}

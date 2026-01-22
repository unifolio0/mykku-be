package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.like.adapter.output.persistence.entity.LikeFeedJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeFeedJpaRepository : JpaRepository<LikeFeedJpaEntity, Long> {
    fun existsByMemberIdAndFeedId(memberId: String, feedId: Long): Boolean
    fun deleteByMemberIdAndFeedId(memberId: String, feedId: Long)
    fun findByMemberIdAndFeedIdIn(memberId: String, feedIds: List<Long>): List<LikeFeedJpaEntity>
    fun deleteAllByFeedId(feedId: Long)
}

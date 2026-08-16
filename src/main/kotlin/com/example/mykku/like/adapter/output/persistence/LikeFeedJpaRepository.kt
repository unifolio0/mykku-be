package com.example.mykku.like.adapter.output.persistence

import com.example.mykku.common.adapter.persistence.IdCountRow
import com.example.mykku.like.adapter.output.persistence.entity.LikeFeedJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LikeFeedJpaRepository : JpaRepository<LikeFeedJpaEntity, Long> {
    fun existsByMemberIdAndFeedId(memberId: Long, feedId: Long): Boolean
    fun deleteByMemberIdAndFeedId(memberId: Long, feedId: Long)
    fun findByMemberIdAndFeedIdIn(memberId: Long, feedIds: List<Long>): List<LikeFeedJpaEntity>
    fun deleteAllByFeedId(feedId: Long)
    fun countByFeedId(feedId: Long): Long

    @Query(
        """
        SELECT l.feed.id AS entityId, COUNT(l) AS countValue
        FROM LikeFeedJpaEntity l
        WHERE l.feed.id IN :feedIds
        GROUP BY l.feed.id
        """
    )
    fun countGroupedByFeedIdIn(@Param("feedIds") feedIds: List<Long>): List<IdCountRow>
}

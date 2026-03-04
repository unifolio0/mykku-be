package com.example.mykku.like.application.port.output

import com.example.mykku.like.domain.entity.LikeFeedEntity

interface LikeFeedPort {
    fun save(likeFeed: LikeFeedEntity): LikeFeedEntity
    fun existsByMemberIdAndFeedId(memberId: Long, feedId: Long): Boolean
    fun deleteByMemberIdAndFeedId(memberId: Long, feedId: Long)
    fun findByMemberIdAndFeedIdIn(memberId: Long, feedIds: List<Long>): List<LikeFeedEntity>
    fun deleteAllByFeedId(feedId: Long)
}

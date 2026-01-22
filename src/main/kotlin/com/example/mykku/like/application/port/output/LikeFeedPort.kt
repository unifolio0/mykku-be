package com.example.mykku.like.application.port.output

import com.example.mykku.like.domain.entity.LikeFeedEntity

interface LikeFeedPort {
    fun save(likeFeed: LikeFeedEntity): LikeFeedEntity
    fun existsByMemberIdAndFeedId(memberId: String, feedId: Long): Boolean
    fun deleteByMemberIdAndFeedId(memberId: String, feedId: Long)
    fun findByMemberIdAndFeedIdIn(memberId: String, feedIds: List<Long>): List<LikeFeedEntity>
    fun deleteAllByFeedId(feedId: Long)
}

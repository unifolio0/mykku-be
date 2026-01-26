package com.example.mykku.feed.application.port.output

import com.example.mykku.feed.domain.entity.FeedTag
import com.example.mykku.feed.domain.vo.FeedId

interface FeedTagRepository {
    fun saveAll(feedTags: List<FeedTag>, feedId: FeedId): List<FeedTag>
    fun findByFeedId(feedId: FeedId): List<FeedTag>
    fun findByFeedIds(feedIds: List<FeedId>): List<FeedTag>
    fun deleteAllByFeedId(feedId: FeedId)
}

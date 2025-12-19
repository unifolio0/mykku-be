package com.example.mykku.feed.application.port.out

import com.example.mykku.feed.domain.model.FeedDomain
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.member.domain.model.MemberId

interface FeedRepositoryPort {
    fun save(feed: FeedDomain): FeedDomain
    fun findById(id: FeedId): FeedDomain?
    fun findByAuthorId(authorId: MemberId): List<FeedDomain>
    fun existsById(id: FeedId): Boolean
    fun delete(feed: FeedDomain)
}

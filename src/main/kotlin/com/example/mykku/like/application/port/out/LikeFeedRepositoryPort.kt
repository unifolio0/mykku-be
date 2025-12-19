package com.example.mykku.like.application.port.out

import com.example.mykku.feed.domain.Feed
import com.example.mykku.like.domain.LikeFeed
import com.example.mykku.member.domain.Member

interface LikeFeedRepositoryPort {
    fun createLikeFeed(feed: Feed, member: Member): LikeFeed
    fun deleteLikeFeed(memberId: String, feedId: Long)
}

package com.example.mykku.member.repository

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.SaveFeed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveFeedRepository : JpaRepository<SaveFeed, Long> {
    fun existsByMemberIdAndFeed(memberId: String, feed: Feed): Boolean
}

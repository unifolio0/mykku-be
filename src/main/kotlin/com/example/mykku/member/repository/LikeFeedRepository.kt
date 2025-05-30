package com.example.mykku.member.repository

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.LikeFeed
import com.example.mykku.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeFeedRepository : JpaRepository<LikeFeed, Long> {
    fun existsByMemberAndFeed(member: Member, feed: Feed): Boolean
}

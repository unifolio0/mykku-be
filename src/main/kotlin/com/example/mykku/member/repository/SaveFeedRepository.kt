package com.example.mykku.member.repository

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.SaveFeed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveFeedRepository : JpaRepository<SaveFeed, Long> {
    fun existsByMemberAndFeed(member: Member, feed: Feed): Boolean
}

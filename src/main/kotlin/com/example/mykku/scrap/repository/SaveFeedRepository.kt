package com.example.mykku.scrap.repository

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveFeed
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaveFeedRepository : JpaRepository<SaveFeed, Long> {
    fun existsByMemberAndFeed(member: Member, feed: Feed): Boolean
    fun findByMemberAndFeedIdIn(member: Member, feedIds: List<Long>): List<SaveFeed>
    fun findByMember(member: Member, pageable: Pageable): Page<SaveFeed>
    fun findByMemberAndFolder(member: Member, folder: Folder?, pageable: Pageable): Page<SaveFeed>
    fun findByMemberAndFeed(member: Member, feed: Feed): SaveFeed?
    fun deleteByMemberAndFeed(member: Member, feed: Feed)

    fun deleteAllByFeed(feed: Feed)
}

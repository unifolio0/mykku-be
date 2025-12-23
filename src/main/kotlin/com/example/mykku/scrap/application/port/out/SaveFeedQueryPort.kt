package com.example.mykku.scrap.application.port.out

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveFeed
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SaveFeedQueryPort {
    fun isSaved(member: Member, feed: Feed): Boolean
    fun isSaved(memberId: String, feed: Feed): Boolean
    fun getSavedFeedsByMember(member: Member, feeds: List<Feed>): Set<Long>
    fun getSavedFeedsByMember(memberId: String, feeds: List<Feed>): Set<Long>
    fun getSavedFeeds(member: Member, pageable: Pageable): Page<SaveFeed>
    fun getSavedFeedsByFolder(member: Member, folder: Folder?, pageable: Pageable): Page<SaveFeed>
}

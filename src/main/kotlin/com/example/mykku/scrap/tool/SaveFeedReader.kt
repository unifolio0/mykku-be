package com.example.mykku.scrap.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.MemberRepository
import com.example.mykku.scrap.domain.Folder
import com.example.mykku.scrap.domain.SaveFeed
import com.example.mykku.scrap.repository.SaveFeedRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class SaveFeedReader(
    private val saveFeedRepository: SaveFeedRepository,
    private val memberRepository: MemberRepository
) {
    fun isSaved(member: Member, feed: Feed): Boolean {
        return saveFeedRepository.existsByMemberAndFeed(member, feed)
    }

    fun isSaved(memberId: String, feed: Feed): Boolean {
        val member = memberRepository.findByIdOrNull(memberId) ?: return false
        return isSaved(member, feed)
    }

    fun getSavedFeedsByMember(member: Member, feeds: List<Feed>): Set<Long> {
        val feedIds = feeds.mapNotNull { it.id }
        return saveFeedRepository.findByMemberAndFeedIdIn(member, feedIds)
            .map { it.feed.id!! }
            .toSet()
    }

    fun getSavedFeedsByMember(memberId: String, feeds: List<Feed>): Set<Long> {
        val member = memberRepository.findByIdOrNull(memberId) ?: return emptySet()
        return getSavedFeedsByMember(member, feeds)
    }

    fun getSavedFeeds(member: Member, pageable: Pageable): Page<SaveFeed> {
        return saveFeedRepository.findByMember(member, pageable)
    }

    fun getSavedFeedsByFolder(member: Member, folder: Folder?, pageable: Pageable): Page<SaveFeed> {
        return saveFeedRepository.findByMemberAndFolder(member, folder, pageable)
    }
}

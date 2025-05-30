package com.example.mykku.member.tool

import com.example.mykku.feed.domain.Feed
import com.example.mykku.member.domain.Member
import com.example.mykku.member.repository.LikeFeedRepository
import org.springframework.stereotype.Component

@Component
class LikeFeedReader(
    private val likeFeedRepository: LikeFeedRepository,
) {
    fun isLiked(member: Member, feed: Feed): Boolean {
        return likeFeedRepository.existsByMemberAndFeed(member, feed)
    }
}

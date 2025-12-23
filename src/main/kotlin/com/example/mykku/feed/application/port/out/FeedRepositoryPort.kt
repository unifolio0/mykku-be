package com.example.mykku.feed.application.port.out

import com.example.mykku.board.domain.Board
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.domain.model.FeedDomain
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.image.dto.ImageUploadResult
import com.example.mykku.member.domain.Member
import com.example.mykku.member.domain.model.MemberId

interface FeedRepositoryPort {
    fun save(feed: FeedDomain): FeedDomain
    fun findById(id: FeedId): FeedDomain?
    fun findByAuthorId(authorId: MemberId): List<FeedDomain>
    fun existsById(id: FeedId): Boolean
    fun delete(feed: FeedDomain)

    // Legacy write methods for cross-domain compatibility
    fun createFeed(
        title: String,
        content: String,
        board: Board,
        member: Member,
        imageResults: List<ImageUploadResult>,
        tagTitles: List<String>
    ): Triple<Feed, List<FeedImage>, List<FeedTag>>
}

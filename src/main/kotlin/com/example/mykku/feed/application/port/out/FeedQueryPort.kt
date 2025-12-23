package com.example.mykku.feed.application.port.out

import com.example.mykku.board.domain.Board
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.feed.dto.FeedPreviewResponse
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

data class FeedSummary(
    val id: Long,
    val title: String,
    val authorId: String,
    val boardId: Long
)

interface FeedQueryPort {
    fun findSummaryById(id: FeedId): FeedSummary?
    fun existsById(id: FeedId): Boolean

    // Cross-domain methods (returns JPA Entity for other domains)
    fun getFeedById(feedId: Long): Feed
    fun getFeedsByFollower(members: List<Member>): List<Feed>
    fun getFeedsByMembersWithPagination(members: List<Member>, pageable: Pageable): Page<Feed>
    fun getFeedsByBoardWithPagination(board: Board, pageable: Pageable): Page<Feed>
    fun getFeedImagesByFeed(feed: Feed): List<FeedImage>
    fun getFeedTagsByFeed(feed: Feed): List<FeedTag>
    fun getContestTagsByTitles(titles: List<String>): List<ContestTag>
    fun getFeedPreviews(): List<FeedPreviewResponse>
    fun getFeedImagesByFeeds(feeds: List<Feed>): Map<Long, List<FeedImage>>
    fun getFeedTagsByFeeds(feeds: List<Feed>): Map<Long, List<FeedTag>>
    fun getContestTagsByFeedTags(feedTags: List<FeedTag>): Map<String, ContestTag>
}

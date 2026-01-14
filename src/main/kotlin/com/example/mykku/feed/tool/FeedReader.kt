package com.example.mykku.feed.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.repository.ContestTagRepository
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.dto.FeedPreviewResponse
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.repository.FeedCommentRepository
import com.example.mykku.feed.repository.FeedImageRepository
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.feed.repository.FeedTagRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FeedReader(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val contestTagRepository: ContestTagRepository
) {
    fun getFeedPreviews(): List<FeedPreviewResponse> {
        return feedRepository.findAll()
            .map { feed -> FeedPreviewResponse(feed) }
            .take(5)
    }

    fun getFeedById(feedId: Long): Feed {
        return feedRepository.findById(feedId)
            .orElseThrow { FeedException.feedNotFound() }
    }

    fun getFeedsByBoardWithPagination(board: Board, pageable: Pageable): Page<Feed> {
        return feedRepository.findAllByBoardOrderByCreatedAtDesc(board, pageable)
    }

    fun getFeedImagesByFeed(feed: Feed): List<FeedImage> {
        return feedImageRepository.findByFeed(feed)
    }

    fun getFeedTagsByFeed(feed: Feed): List<FeedTag> {
        return feedTagRepository.findByFeed(feed)
    }

    fun getFeedCommentsByFeed(feed: Feed, pageable: Pageable): Page<FeedComment> {
        return feedCommentRepository.findByFeedAndParentCommentIsNull(feed, pageable)
    }

    fun getContestTagsByTitles(titles: List<String>): List<ContestTag> {
        return contestTagRepository.findAllByTitleIn(titles)
    }

    fun getFeedImagesByFeeds(feeds: List<Feed>): Map<Long, List<FeedImage>> {
        val images = feedImageRepository.findByFeedIn(feeds)
        return images.groupBy { it.feed.id!! }
    }

    fun getFeedTagsByFeeds(feeds: List<Feed>): Map<Long, List<FeedTag>> {
        val tags = feedTagRepository.findByFeedIn(feeds)
        return tags.groupBy { it.feed.id!! }
    }

    fun getContestTagsByFeedTags(feedTags: List<FeedTag>): Map<String, ContestTag> {
        val titles = feedTags.map { it.title }.distinct()
        val contestTags = contestTagRepository.findAllByTitleIn(titles)
        return contestTags.associateBy { it.title }
    }

    fun getCommentIdsByFeed(feed: Feed): List<Long> {
        return feedCommentRepository.findIdsByFeed(feed)
    }

    fun getPopularFeedsByBoard(board: Board, limit: Int = 3, daysAgo: Int = 7): List<Feed> {
        val since = LocalDateTime.now().minusDays(daysAgo.toLong())
        val pageable = PageRequest.of(0, limit)
        return feedRepository.findPopularFeedsByBoardSince(board, since, pageable)
    }
}

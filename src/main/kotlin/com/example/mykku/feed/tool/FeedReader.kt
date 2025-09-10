package com.example.mykku.feed.tool

import com.example.mykku.board.domain.Board
import com.example.mykku.exception.ErrorCode
import com.example.mykku.exception.MykkuException
import com.example.mykku.feed.domain.EventTag
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedComment
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.dto.FeedPreviewResponse
import com.example.mykku.feed.repository.EventTagRepository
import com.example.mykku.feed.repository.FeedCommentRepository
import com.example.mykku.feed.repository.FeedImageRepository
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.feed.repository.FeedTagRepository
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FeedReader(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val feedCommentRepository: FeedCommentRepository,
    private val eventTagRepository: EventTagRepository
) {
    fun getFeedPreviews(): List<FeedPreviewResponse> {
        return feedRepository.findAll()
            .map { feed -> FeedPreviewResponse(feed) }
            .take(5)
    }

    fun getFeedsByFollower(members: List<Member>): List<Feed> {
        return feedRepository.findAllByMemberIn(members)
    }

    fun getFeedById(feedId: Long): Feed {
        return feedRepository.findById(feedId)
            .orElseThrow { MykkuException(ErrorCode.FEED_NOT_FOUND) }
    }
    
    fun getFeedsByMembersWithPagination(members: List<Member>, pageable: Pageable): Page<Feed> {
        return if (members.isNotEmpty()) {
            feedRepository.findAllByMemberInOrderByCreatedAtDesc(members, pageable)
        } else {
            Page.empty(pageable)
        }
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
    
    fun getEventTagsByTitles(titles: List<String>): List<EventTag> {
        return eventTagRepository.findAllByTitleIn(titles)
    }
    
    // Batch methods to prevent N+1 queries
    fun getFeedImagesByFeeds(feeds: List<Feed>): Map<Long, List<FeedImage>> {
        val images = feedImageRepository.findByFeedIn(feeds)
        return images.groupBy { it.feed.id!! }
    }
    
    fun getFeedTagsByFeeds(feeds: List<Feed>): Map<Long, List<FeedTag>> {
        val tags = feedTagRepository.findByFeedIn(feeds)
        return tags.groupBy { it.feed.id!! }
    }
    
    fun getFeedCommentsByFeeds(feeds: List<Feed>, pageable: Pageable): Map<Long, Page<FeedComment>> {
        val result = mutableMapOf<Long, Page<FeedComment>>()
        feeds.forEach { feed ->
            result[feed.id!!] = feedCommentRepository.findByFeedAndParentCommentIsNull(feed, pageable)
        }
        return result
    }
    
    fun getEventTagsByFeedTags(feedTags: List<FeedTag>): Map<String, EventTag> {
        val titles = feedTags.map { it.title }.distinct()
        val eventTags = eventTagRepository.findAllByTitleIn(titles)
        return eventTags.associateBy { it.title }
    }
}

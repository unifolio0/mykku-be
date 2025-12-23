package com.example.mykku.feed.infrastructure.adapter

import com.example.mykku.board.domain.Board
import com.example.mykku.contest.domain.ContestTag
import com.example.mykku.contest.repository.ContestTagRepository
import com.example.mykku.feed.application.port.out.FeedQueryPort
import com.example.mykku.feed.application.port.out.FeedSummary
import com.example.mykku.feed.domain.Feed
import com.example.mykku.feed.domain.FeedImage
import com.example.mykku.feed.domain.FeedTag
import com.example.mykku.feed.domain.model.FeedId
import com.example.mykku.feed.dto.FeedPreviewResponse
import com.example.mykku.feed.exception.FeedException
import com.example.mykku.feed.repository.FeedImageRepository
import com.example.mykku.feed.repository.FeedRepository
import com.example.mykku.feed.repository.FeedTagRepository
import com.example.mykku.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FeedQueryAdapter(
    private val feedRepository: FeedRepository,
    private val feedImageRepository: FeedImageRepository,
    private val feedTagRepository: FeedTagRepository,
    private val contestTagRepository: ContestTagRepository
) : FeedQueryPort {

    override fun findSummaryById(id: FeedId): FeedSummary? {
        return feedRepository.findById(id.value)
            .map { feed ->
                FeedSummary(
                    id = feed.id!!,
                    title = feed.title,
                    authorId = feed.member.id,
                    boardId = feed.board.id!!
                )
            }
            .orElse(null)
    }

    override fun existsById(id: FeedId): Boolean {
        return feedRepository.existsById(id.value)
    }

    override fun getFeedById(feedId: Long): Feed {
        return feedRepository.findById(feedId)
            .orElseThrow { FeedException.feedNotFound() }
    }

    override fun getFeedsByFollower(members: List<Member>): List<Feed> {
        return feedRepository.findAllByMemberIn(members)
    }

    override fun getFeedsByMembersWithPagination(members: List<Member>, pageable: Pageable): Page<Feed> {
        return if (members.isNotEmpty()) {
            feedRepository.findAllByMemberInOrderByCreatedAtDesc(members, pageable)
        } else {
            Page.empty(pageable)
        }
    }

    override fun getFeedsByBoardWithPagination(board: Board, pageable: Pageable): Page<Feed> {
        return feedRepository.findAllByBoardOrderByCreatedAtDesc(board, pageable)
    }

    override fun getFeedImagesByFeed(feed: Feed): List<FeedImage> {
        return feedImageRepository.findByFeed(feed)
    }

    override fun getFeedTagsByFeed(feed: Feed): List<FeedTag> {
        return feedTagRepository.findByFeed(feed)
    }

    override fun getContestTagsByTitles(titles: List<String>): List<ContestTag> {
        return contestTagRepository.findAllByTitleIn(titles)
    }

    override fun getFeedPreviews(): List<FeedPreviewResponse> {
        return feedRepository.findAll()
            .map { feed -> FeedPreviewResponse(feed) }
            .take(5)
    }

    override fun getFeedImagesByFeeds(feeds: List<Feed>): Map<Long, List<FeedImage>> {
        val images = feedImageRepository.findByFeedIn(feeds)
        return images.groupBy { it.feed.id!! }
    }

    override fun getFeedTagsByFeeds(feeds: List<Feed>): Map<Long, List<FeedTag>> {
        val tags = feedTagRepository.findByFeedIn(feeds)
        return tags.groupBy { it.feed.id!! }
    }

    override fun getContestTagsByFeedTags(feedTags: List<FeedTag>): Map<String, ContestTag> {
        val titles = feedTags.map { it.title }.distinct()
        val contestTags = contestTagRepository.findAllByTitleIn(titles)
        return contestTags.associateBy { it.title }
    }
}
